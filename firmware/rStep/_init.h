#ifndef _INIT_
#define _INIT_

//constants
#define MAX_COMMANDS 8 //max arguments on one line
#define COMMAND_SIZE 64 //max length of input command
//default stepper mode
#define DEFAULT_STEP sixteenth
//available stepper modes
#define full 1
#define half 2
#define quarter 4
#define eighth 8
#define sixteenth 16
//misc definitions
#define FORWARD 1
#define BACKWARD -1
#define CHAN_X 0
#define CHAN_Y 1
#define CHAN_Z 2
//misc util functions
#define SBI(port,pin) port |= _BV(pin);
#define CBI(port,pin) port &= ~_BV(pin);
#define error(x) Serial.println(x)
#define debug(x) Serial.println(x)
#define debug2(x,y) Serial.print(x);Serial.println(y)
#define debug3(x,y,z) Serial.print(x);Serial.println(y,z)
#define disable_steppers() digitalWrite(ENABLE, HIGH)
#define enable_steppers()  digitalWrite(ENABLE, LOW)
#define intDisable()      ({ uint8_t sreg = SREG; cli(); sreg; })
#define intRestore(sreg)  SREG = sreg 
#define myMicros() (micros() >> 3)
#define LED_ON() digitalWrite(17,LOW);
#define LED_OFF() digitalWrite(17,HIGH);
//pin definitions
/****************************************************************************************/
//my config
#define ENABLE 18 //low enabled
#define MS1 2
#define MS2 4
#define RST 3 
#define SLP 3
#define STEP_X 9
#define STEP_Y 10
#define STEP_Z 11
#define DIR_X 8
#define DIR_Y 12
#define DIR_Z 19
#define MOTOR_PIN 6

/* specify the port and pin numbers - you need to look this up 
** on the schematic for the particular arduino */
#define _STEP_PORT PORTB
#define _STEP_DDR  DDRB
#define _STEP_X    (1<<1)
#define _STEP_Y    (1<<2)
#define _STEP_Z    (1<<3)


// specify min-max sense pins or 0 if not used
// active low is prefered as it will cause the AVR to use it's internal pullups to 
// avoid bounce on the line.  If you want active_high, then you must add external pulldowns
// to avoid false signals.
#define MINMAX_X 7 
#define MINMAX_Y 15
#define MINMAX_Z 5

// STRUCTURES
struct u16_t {
  uint16_t x;
  uint16_t y;
  uint16_t z;
};
struct u8_t {
  uint8_t x;
  uint8_t y;
  uint8_t z;
};
struct FloatPoint {
  float x;
  float y;
  float z;
};

struct axis_t {
  uint8_t step_pin;
  uint8_t direct_step_pin;
  uint8_t minMax_pin;
  float current_units;
  float target_units;
  float delta_units;
  uint32_t delta_steps;
  uint16_t timePerStep;
  int8_t direction; //FORWARD or BACKWARD
  uint32_t nextEvent;
};
typedef struct axis_t *axis;

struct command_t {
	uint8_t type; //i.e. G or M
	double  value; //string value associated
	//struct command_t *next;
};
typedef struct command_t *command;

typedef struct config_t {
  struct u16_t steps_inch; //M101
  struct u8_t  max_feedrate; //M102
  struct u8_t  current; //M100
  uint8_t      stepping; //M103 Sx
  bool         abs_mode; //G90, G91
};





#endif
