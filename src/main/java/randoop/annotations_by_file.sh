#!/bin/bash

for f in `find . -type f -name '*.java'`
do
    count=`grep -E '@PolyDet|@Det' $f | wc -l`
    echo "$count $f"
done
