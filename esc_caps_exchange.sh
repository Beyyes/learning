#!/bin/sh

# capsLock 和 Esc

xmodmap - <<EOF
remove Lock = Caps_Lock
keysym Escape = Caps_Lock
keysym Caps_Lock = Escape
add Lock = Caps_Lock
EOF
echo 
