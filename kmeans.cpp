/**
* K-means Algorithm Implemantation using MPI

* @author CGF
* 
*/
#include <iostream>
#include <vector>
#include <stdio.h>
#include <time.h>
#include <math.h>
#include "stdlib.h"
#include "mpi.h"

using namespace std;


typedef struct 
{
	double x, y;
} Point;

const int MASTER = 0;

static int num_points;

static int num_clusters;

/**
* calc the distance between two points
**/
double calcDistance(Point p1, Point p2);

/**
* to determine the center of a point
**/
int judgeCenters(Point point, Point* centers);

/**
* to determine the center of the new cluster
**/
void calcNewcenters(Point* points, int* new_cluster, Point* centers);

/**
* check the old data and new data to determine if convergence
**/
bool check(int* older_clusters, int* newer_clusters);

int main(int argc, char* argv[])
{

	int i;
	int rank, size;
	int part_size;     // number of points of each cluster
	int done_flag = 0; // to determine whether the algorithm is completed

	Point* centers;
	Point* all_points;
	Point* received_points;
	int  * slave_clusters;  // represent the center of each slave points
	int  * older_clusters;
	int  * newer_clusters;

	MPI_Init(&argc, &argv);

	MPI_Status status;

	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &size);

	// derived MPI structure
	MPI_Datatype MPI_POINT;
	MPI_Datatype type = MPI_DOUBLE;
	int blocklen = 2;
	MPI_Aint disp = 0;
	MPI_Type_create_struct(1, &blocklen, &disp, &type, &MPI_POINT);
	MPI_Type_commit(&MPI_POINT);

	time_t start_time, stop_time;
    start_time = time(NULL);
    // foo();//dosomething

	// clock;
	// Sending data to slave
	if(rank == MASTER)
	{
		// input data
		FILE *input;
		input = fopen(argv[1], "r");

		fscanf(input, "%d\n", &num_clusters);
		printf("%d\n", num_clusters);
		fscanf(input, "%d\n", &num_points);
		printf("%d\n", num_points);
		
		all_points = new Point[num_points]; 
		for(int i = 0;i < num_points;i++)
		{
			fscanf(input, "%lf,%lf", &all_points[i].x, &all_points[i].y);
		}
		fclose(input);

		older_clusters = new int[num_points];
		newer_clusters = new int[num_points];
		part_size = num_points / (size-1);  
		centers = new Point[num_clusters]; 

		// data init and centers srand		
		srand(time(NULL));
		for(int i = 0;i < num_clusters;i++)
		{
			centers[i].x = ((double)(rand()%1000))/1000;
			centers[i].y = ((double)(2*rand()%1000))/1000;
		}

		for(int i = 0;i < num_points;i++)
		{
			older_clusters[i] = -1;
			newer_clusters[i] = -1;
		}

		// send data to different processor
		for(int i = 1;i < size;i++)
		{
			printf("Sending to [%d] \n",i);
			MPI_Send(&part_size, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(&num_clusters, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(centers, num_clusters, MPI_POINT, i, 0, MPI_COMM_WORLD);
			MPI_Send(all_points+(i-1) * part_size, part_size, MPI_POINT, i, 0, MPI_COMM_WORLD);
		}
		cout<<"Send Complete!"<<endl;

		MPI_Barrier(MPI_COMM_WORLD);

		//Main job of master processor is done here		
		while(1)
		{	
			MPI_Barrier(MPI_COMM_WORLD);
			cout <<"Master Receiving"<<endl;
			for(int i = 1;i < size;i++)
				MPI_Recv(newer_clusters + (part_size*(i-1)), part_size, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
			cout<<"Master Received"<<endl;

			calcNewcenters(all_points, newer_clusters, centers);
			cout<<"New centers are done!"<<endl;
			if(check(newer_clusters, older_clusters))
			{
				cout<<"Converge."<<endl;
				done_flag = 1;
			}
			else    
			{
				cout<<"Not converge"<<endl;
				for(int i = 0;i < num_points;i++)
					older_clusters[i] = newer_clusters[i];
			}

			// all centers converge
			for(int i = 1;i < size;i++)
				MPI_Send(&done_flag, 1, MPI_INT, i, 0, MPI_COMM_WORLD);

			MPI_Barrier(MPI_COMM_WORLD);
			if(done_flag == 1)
				break;

			// Sending the recently created centers			
			for(int i = 1;i < size;i++)
				MPI_Send(centers, num_clusters, MPI_POINT, i, 0, MPI_COMM_WORLD);

			MPI_Barrier(MPI_COMM_WORLD);
		}

		stop_time = time(NULL);
		printf("Use Time:%ld\n",(stop_time - start_time));

		// output result		
		FILE* output = fopen(argv[2], "w");
		fprintf(output, "%d\n", num_clusters);
		fprintf(output, "%d\n", num_points);
		for(i = 0;i < num_clusters;i++)
			fprintf(output, "%lf,%lf\n", centers[i].x, centers[i].y);
		for(i = 0;i < num_points;i++)
			fprintf(output, "%lf,%lf,%d\n", all_points[i].x, all_points[i].y, newer_clusters[i]+1);
		fclose(output);
	}
	else
	{
		// Receiving data.
		cout<<"Receiving"<<endl;
		MPI_Recv(&part_size, 1, MPI_INT, MASTER, 0, MPI_COMM_WORLD, &status);
		MPI_Recv(&num_clusters, 1, MPI_INT, MASTER, 0, MPI_COMM_WORLD, &status);
		centers = new Point[num_clusters];
		MPI_Recv(centers, num_clusters, MPI_POINT, MASTER, 0, MPI_COMM_WORLD, &status);
		printf("part_size = %d\n", part_size);
		received_points = new Point[part_size];
		slave_clusters = new int[part_size];
		MPI_Recv(received_points, part_size, MPI_POINT, MASTER, 0, MPI_COMM_WORLD, &status);
		printf("Received [%d]\n", rank);

		MPI_Barrier(MPI_COMM_WORLD);

		while(1)
		{
			printf("Calculation of new clusters [%d]\n",rank);
			for(int i = 0; i < part_size; i++)
			{
				slave_clusters[i] = judgeCenters(received_points[i], centers);
			}

			printf("sending to master [%d]\n",rank);
			MPI_Send(slave_clusters, part_size, MPI_INT, MASTER, 0, MPI_COMM_WORLD);
			MPI_Barrier(MPI_COMM_WORLD);
			MPI_Barrier(MPI_COMM_WORLD);
			MPI_Recv(&done_flag, 1, MPI_INT, MASTER, 0, MPI_COMM_WORLD, &status);

			if(done_flag == 1) 
				break;

			//Receiving recently created centers from master
			MPI_Recv(centers, num_clusters, MPI_POINT, MASTER, 0, MPI_COMM_WORLD, &status);

			MPI_Barrier(MPI_COMM_WORLD);
		}
	}
	MPI_Finalize();
	return 0;
}


double calcDistance(Point p1, Point p2)
{
	return (pow((p1.x-p2.x)*100,2) + pow((p1.y-p2.y)*100,2));	
}

int judgeCenters(Point point, Point* centers)
{
	int ans = 0;
	double distance = 0;
	double minDistance = calcDistance(point, centers[0]);

	for(int i = 1;i < num_clusters;i++)
	{	
		distance = calcDistance(point, centers[i]);
		if(minDistance >= distance)
		{ 
			ans = i;
			minDistance = distance;
		}
	}
	return ans;
}


void calcNewcenters(Point* points, int* new_cluster, Point* centers)
{
	Point* newcenters = new Point[num_clusters];
	int* count = new int[num_clusters];
	
	for(int i = 0;i < num_clusters;i++)
	{
		count[i] = 0;
		newcenters[i].x = 0;
		newcenters[i].y = 0;
	}	

	for(int i = 0;i < num_points;i++) 
	{
		count[new_cluster[i]]++;
		newcenters[new_cluster[i]].x += points[i].x;
		newcenters[new_cluster[i]].y += points[i].y;
	}

	for(int i = 0;i < num_clusters;i++)
	{
		if(count[i] != 0.0)
		{
			newcenters[i].x /= count[i];
			newcenters[i].y /= count[i];
		}
	}

	for(int i = 0;i < num_clusters;i++)
	{
		centers[i].x = newcenters[i].x;
		centers[i].y = newcenters[i].y;
	}	

	delete []count;
	delete []newcenters;
}


bool check(int *older_clusters, int *newer_clusters)
{
	// may be optimized only by comparance of k centers
	for(int i = 0;i < num_points;i++)
		if(older_clusters[i] != newer_clusters[i])
			return false;
	return true;
}


