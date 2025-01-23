#!/bin/bash

warnings="-Wextra -Wnull-dereference"
includes="-Ivendor -I*.hpp"
timestamp=$(date +%s)

if [[ "$(uname)" == "Linux" ]]; then
    echo "Running on Linux"
    libs="-Lvendor/raylinux -lraylib -lX11 -lGL -lpthread -lm -ldl"
    outputFile=app
    queryProcesses=$(pgrep $outputFile)

elif [[ "$(uname)" == "Darwin" ]]; then
    echo "Running on Mac"
    # libs="-framework Cocoa"
    # sdkpath=$(xcode-select --print-path)/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk
    # includes="-Ivendor -isysroot ${sdkpath} -I${sdkpath}/System/Library/Frameworks/Cocoa.framework/Headers"
    # objc_dep="src/mac_platform.m"
    # outputFile=app
    # # clean up old object files
    # rm -f src/*.o
else
    echo "Running on Windows"

    libs="-Lvendor/raywin -lraylib -lopengl32 -lgdi32 -lwinmm -lkernel32 -lshell32 -luser32 "
    outputFile=app.exe
    queryProcesses=$(tasklist | grep $outputFile)

fi

processRunning=$queryProcesses

if [ -z "$processRunning" ]; then
    echo "Building main..."
    g++ $warnings $includes src/main.cpp -o $outputFile $libs 
    # Check if the compilation was successful
    if [ $? -eq 0 ]; then
        echo "Compilation successful."
        echo " "
        
        ./$outputFile
    else
        echo "Compilation failed."
    fi
fi

