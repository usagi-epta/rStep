#include <inttypes.h>
#include "_init.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>


bool    abs_mode = true;   //0 = incremental; 1 = absolute
uint8_t stepping = DEFAULT_STEP;
uint8_t command_word[COMMAND_SIZE];
uint8_t serial_count=0;
uint16_t no_data = 0;
uint16_t oldKeys;
axis axis_array[3];
float _feedrate;

struct axis_t xaxis_data;
struct axis_t yaxis_data;
struct axis_t zaxis_data;

axis xaxis;
axis yaxis;
axis zaxis;

void setup() {
  //serial
  Serial.begin(9600);
 
  //init code
//  keypad_init();
  myStepper_init();
  motor_init();
  init_steppers();
  _feedrate = getMaxFeedrate();
  init_process_string();
  //increase clock resolution
  TCCR0B &= ~_BV(CS00); //for ATmega168!! XXX: this will mess up millis,micros,delay,delayMicroseconds
 
  Serial.println("start");
}

void init_process_string() {
  for (byte i=0; i<COMMAND_SIZE; i++) command_word[i] = 0;
  serial_count = 0;
}


void loop() {
  uint8_t c;
  uint16_t newKeys;

  //read in characters if we got them.
  if (Serial.available() > 0)	{
    c = (uint8_t)Serial.read();
    no_data = 0;
    command_word[serial_count++] = c;
  } 
  else {
    no_data++;
    delayMicroseconds(100);
  }

  //if theres a pause or we got a real command, do it
  if (serial_count && (c == '\n' || no_data > 100)) {
    command_word[serial_count] = 0; //change \n to null terminator
    process_string(command_word); //do
    init_process_string(); //clear
  }

/*
  //keypad actions
  newKeys = keypad_scan();
  if (newKeys != oldKeys) {
    Serial.println(newKeys,HEX);
    oldKeys = newKeys;
    switch(newKeys) {
      case _BV(1):
      case _BV(2):
      case _BV(3):
      case _BV(4):
      case _BV(5):
      case _BV(6):
      case _BV(7):
      case _BV(8):
      case _BV(9):
      case _BV(10):
      case _BV(11):
      case _BV(12):
      case _BV(13):
      case _BV(14):
      case _BV(15):
      default:
        break;
    }
  }
*/

  //no data?  turn off steppers
  if (no_data > 1000)  {
    disable_steppers();
  }
}
//foo


