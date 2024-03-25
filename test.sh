#!/bin/bash

java -classpath javacc.jar javacc PA4.jj
javac PA4.java
java PA4 tests/Demo.java