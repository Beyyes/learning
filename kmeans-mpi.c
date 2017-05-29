/**
* K-means Algorithm Implemantation using MPI
* @author CGF
* 
*/
#include <iostream>
#include <stdio.h>
#include "stdlib.h"
#include <time.h>
#include <math.h>
#include "mpi.h"

using namespace std;

#define MASTER 0

/**
* Point Defination
*/
typedef struct 
{
	double _x;
	double _y;
} Point;

/**
* reader function of the input file's first(for number of clusters)  
* & second(for number of points) line
* @param input input file handler
* @param num_clusters pointer to return number of clusters
* @param num_points pointer to return number of points	
*/
void readHeaders(FILE *input,int* num_clusters,int* num_points)
{
	fscanf(input,"%d\n",num_clusters);
	printf("%d\n",*num_clusters);

	fscanf(input,"%d\n",num_points);
	printf("%d\n",*num_points);
}

/**
* reader function of the points in the input file
* This function must be called after  readHeaders(...) function
* @param input input file handler
* @param points pointer to return the array of points
* @param num_points number of points to read
*/
void readPoints(FILE* input,Point *points,int num_points)
{
	int dex;
	for(dex=0;dex<num_points;dex++)
	{
		fscanf(input,"%lf,%lf",&points[dex]._x,&points[dex]._y);
	}
}
/**
* initializer function that randomly initialize the centroids
* @param centroids pointer to return array of centroids
* @param num_cluster number of clusters(so number of centroids, too)
*/
void initialize(Point* centroids,int num_clusters)
{
	int dex;
	srand(time(NULL));
	for(dex=0;dex<num_clusters;dex++)
	{
		centroids[dex]._x=((double)(rand()%1000))/1000;
		centroids[dex]._y=((double)(2*rand()%1000))/1000;
	}
}
/**
* initializer function that initializes the all cluster array values to -1
* @param data pointer to return array of cluster data
* @param num_points number of points to initialize
*/
void resetData(int *data,int num_points)
{
	int dex;
	for(dex=0;dex<num_points;dex++)
	{
		data[dex]=-1;
	}		
}
/**
* calculate distance between two points
* @param point1 first point
* @param point2 second point
* @return distance in double precision
*/
double calculateDistance(Point point1,Point point2)
{
	return (pow((point1._x-point2._x)*100,2)+pow((point1._y-point2._y)*100,2));	
}
/**
* Wierd name but essential function; decides witch centroid is closer to the given point
* @param point point given
* @param centroids pointer to centroids array
* @param num_centroids number of centroids to check
* @return closest centroid's index in centroids array(2nd param)
*/
int whoIsYourDaddy(Point point,Point* centroids,int num_centroids)
{
	int daddy=0;
	double distance=0;
	double minDistance=calculateDistance(point,centroids[0]);
	int dex;

	for(dex=1;dex<num_centroids;dex++)
	{	
		distance=calculateDistance(point,centroids[dex]);
		if(minDistance>=distance)
		{
			daddy=dex;
			minDistance=distance;
		}
	}
	return daddy;
}
/**
* Cumulative function that must be called after the closest centroid for each point is found
* Calculates new centroids as describen in kmeans algorithm
* @param points array of points
* @param data array of cluster assignments
* @param centroids return array of centroids
* @param num_clusters number of clusters(so number of centroids)
* @param num_points number of points 
*/
void calculateNewCentroids(Point* points,int* data,Point* centroids,int num_clusters,int num_points)
{
	Point* newCentroids=(Point*)malloc(sizeof(Point)*num_clusters);
	int* population=(int*)malloc(sizeof(int)*num_clusters);
	int i;

	for(i=0;i<num_clusters;i++)
	{
		population[i]=0;
		newCentroids[i]._x=0;
		newCentroids[i]._y=0;
	}	
	for(i=0;i<num_points;i++)
	{
		population[data[i]]++;
		newCentroids[data[i]]._x+=points[i]._x;
		newCentroids[data[i]]._y+=points[i]._y;
	}
	for(i=0;i<num_clusters;i++)
	{
		if(population[i]!=0.0)
		{
			newCentroids[i]._x/=population[i];
			newCentroids[i]._y/=population[i];
		}
	}
	for(i=0;i<num_clusters;i++)
	{
		centroids[i]._x=newCentroids[i]._x;
		centroids[i]._y=newCentroids[i]._y;
	}	
}
/**
* Convergence checker (see project description for further info)
* @param former_clusters pointer to array of older cluster assignments
* @param latter_clusters pointer to array of newer cluster assignments
* @param num_points number of points 
* @return -1 if not converged, 0 if converged.
*/
int checkConvergence(int *former_clusters,int *latter_clusters,int num_points)
{
	int dex;
	for(dex=0;dex<num_points;dex++)
		if(former_clusters[dex]!=latter_clusters[dex])
			return -1;
	return 0;
}
/**
* main function
* divided to two brances for master & slave processors respectively
* @param argc commandline argument count
* @param argv array of commandline arguments
* @return 0 if success
*/
int main(int argc, char* argv[])
{
	int rank;
	int size;
	int num_clusters;  // 聚类数目
	int num_points;
	int dex;
	int job_size;
	int job_done=0;

	Point* centroids;
	Point* points;
	Point* received_points;
	int  * slave_clusters;
	int  * former_clusters;
	int  * latter_clusters;

	MPI_Init(&argc, &argv);

	MPI_Status status;

	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &size);

	//creation of derived MPI structure
	MPI_Datatype MPI_POINT;
	MPI_Datatype type=MPI_DOUBLE;
	int blocklen=2;
	MPI_Aint disp=0;
	MPI_Type_create_struct(1,&blocklen,&disp,&type,&MPI_POINT);
	MPI_Type_commit(&MPI_POINT);

	/******** MASTER PROCESSOR WORKS HERE******************************************************/ 

	if(rank==MASTER)
	{
		//inputting from file
		FILE *input;
		input=fopen(argv[1],"r");
		readHeaders(input,&num_clusters,&num_points);
		points=(Point*)malloc(sizeof(Point)*num_points);
		readPoints(input,points,num_points);
		fclose(input);

		//other needed memory locations
		former_clusters=(int*)malloc(sizeof(int)*num_points);
		latter_clusters=(int*)malloc(sizeof(int)*num_points);
		job_size=num_points/(size-1);   // job_size 每个聚类的点数目
		centroids=(Point*)malloc(sizeof(Point)*num_clusters);  // 每个聚类簇中心

		//reseting and initializing to default behaviour		
		initialize(centroids,num_clusters);
		resetData(former_clusters,num_points);
		resetData(latter_clusters,num_points);

		// 将数据分发到不同的进程
		for(dex=1;dex<size;dex++)
		{
			printf("Sending to [%d]\n",dex);
			MPI_Send(&job_size              ,1           , MPI_INT        ,dex,0,MPI_COMM_WORLD);
			MPI_Send(&num_clusters          ,1           , MPI_INT        ,dex,0,MPI_COMM_WORLD);
			MPI_Send(centroids              ,num_clusters, MPI_POINT      ,dex,0,MPI_COMM_WORLD);
			MPI_Send(points+(dex-1)*job_size,job_size    , MPI_POINT      ,dex,0,MPI_COMM_WORLD);
		}
		printf("Send Complete!\n");

		MPI_Barrier(MPI_COMM_WORLD);

		//Main job of master processor is done here		
		while(1)
		{	
			MPI_Barrier(MPI_COMM_WORLD);

			printf("Master Receiving\n");
			for(dex=1;dex<size;dex++)
				MPI_Recv(latter_clusters+(job_size*(dex-1)),job_size,MPI_INT,dex,0,MPI_COMM_WORLD,&status);

			printf("Master Received\n");

			calculateNewCentroids(points,latter_clusters,centroids,num_clusters,num_points);
			printf("New Centroids are done!\n");
			if(checkConvergence(latter_clusters,former_clusters,num_points)==0)
			{
				printf("Converged!\n");
				job_done=1;
			}
			else    
			{
				printf("Not converged!\n");
				for(dex=0;dex<num_points;dex++)
					former_clusters[dex]=latter_clusters[dex];
			}

			//Informing slaves that no more job to be done
			for(dex=1;dex<size;dex++)
				MPI_Send(&job_done,1, MPI_INT,dex,0,MPI_COMM_WORLD);

			MPI_Barrier(MPI_COMM_WORLD);
			if(job_done==1)
				break;

			//Sending the recently created centroids			
			for(dex=1;dex<size;dex++)
				MPI_Send(centroids,num_clusters, MPI_POINT,dex,0, MPI_COMM_WORLD);

			MPI_Barrier(MPI_COMM_WORLD);
		}

		//Outputting to the output file		
		FILE* output=fopen(argv[2],"w");
		fprintf(output,"%d\n",num_clusters);
		fprintf(output,"%d\n",num_points);
		for(dex=0;dex<num_clusters;dex++)
			fprintf(output,"%lf,%lf\n",centroids[dex]._x,centroids[dex]._y);
		for(dex=0;dex<num_points;dex++)
			fprintf(output,"%lf,%lf,%d\n",points[dex]._x,points[dex]._y,latter_clusters[dex]+1);
		fclose(output);
	}
	/*************END OF MASTER PROCESSOR'S BRANCH -- SLAVE PROCESSORS' JOB IS TO FOLLOW ************************/
	else
	{
		//Receiving the essential data
		printf("Receiving\n");
		MPI_Recv(&job_size    ,1           ,MPI_INT  ,MASTER,0,MPI_COMM_WORLD,&status);
		MPI_Recv(&num_clusters,1           ,MPI_INT  ,MASTER,0,MPI_COMM_WORLD,&status);
		centroids=(Point*)malloc(sizeof(Point)*num_clusters);
		MPI_Recv(centroids    ,num_clusters,MPI_POINT,MASTER,0,MPI_COMM_WORLD,&status);
		printf("part_size =%d\n",job_size);
		received_points=(Point*)malloc(sizeof(Point)*job_size);
		slave_clusters=(int*)malloc(sizeof(int)*job_size);
		MPI_Recv(received_points,job_size,MPI_POINT      ,MASTER,0,MPI_COMM_WORLD,&status);
		printf("Received [%d]\n",rank);

		MPI_Barrier(MPI_COMM_WORLD);

		while(1)
		{
			printf("Calculation of new clusters [%d]\n",rank);
			for(dex=0;dex<job_size;dex++)
			{
				slave_clusters[dex]=whoIsYourDaddy(received_points[dex],centroids,num_clusters);
			}

			printf("sending to master [%d]\n",rank);
			MPI_Send(slave_clusters,job_size, MPI_INT,MASTER, 0, MPI_COMM_WORLD);
			MPI_Barrier(MPI_COMM_WORLD);
			MPI_Barrier(MPI_COMM_WORLD);
			MPI_Recv(&job_done,1, MPI_INT,MASTER,0,MPI_COMM_WORLD,&status);

			if(job_done==1) //No more work to be done
				break;

			//Receiving recently created centroids from master
			MPI_Recv(centroids,num_clusters,MPI_POINT,MASTER,0, MPI_COMM_WORLD,&status);

			MPI_Barrier(MPI_COMM_WORLD);
		}
	}
	//End of all	
	MPI_Finalize();
	return 0;
}
/* EOF */
