CC=`which gcc`
CFLAGS=-g -Wall -O2 
TARGET=gcodeInject

all: $(TARGET)

$(TARGET): serial.o main.o
	$(CC) $(CFLAGS) -o $(TARGET) serial.o main.o

clean: 
	rm -rf *.o $(TARGET)

install: all
	mkdir -p /usr/local/bin
	cp $(TARGET) /usr/local/bin

zip:
	zip rstep_inject.zip serial.c main.c Makefile

