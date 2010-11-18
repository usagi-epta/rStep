/*
// define the parameters of our machine.
 #define X_STEPS_PER_INCH 4064.0
 #define Y_STEPS_PER_INCH 4064.0
 #define Z_STEPS_PER_INCH 4064.0
 
 //our maximum feedrates
 #define MAX_X_FEEDRATE 15.0
 #define MAX_Y_FEEDRATE 15.0
 #define MAX_Z_FEEDRATE 13.0
 
 typedef struct config_t {
  struct u16_t steps_inch; //M101
  struct u8_t  max_feedrate; //M102
  struct u8_t  current; //M100
  uint8_t      stepping; //M103 Sx
  bool         abs_mode; //G90, G91
  uint8_t      dir; //M104 [XYZ](0|1)
  uint8_t      motorSpeed; //M105 S(1-255)
};

 */

// Units in curve section
#define CURVE_SECTION_INCHES 0.019685
#define CURVE_SECTION_MM 0.5


void config_save(void) {
  uint16_t i;
  uint8_t *ptr = (uint8_t*)(&config);
  for (i=0; i<sizeof(struct config_t); i++) {
    EEPROM.write(i,ptr[i]);
  }
}

void config_read(void) {
  uint16_t i;
  uint8_t *ptr = (uint8_t*)(&config);
  for (i=0; i<sizeof(struct config_t); i++) {
    ptr[i] = EEPROM.read(i);
  }
}

void config_dump(void) {
  Message3F("SBI", config.steps_inch.x, config.steps_inch.y, config.steps_inch.z, DEC);
  Message3F("MFR", config.max_feedrate.x, config.max_feedrate.y, config.max_feedrate.z, DEC);
  Message3F("Cur", config.current.x, config.current.y, config.current.z, DEC);
  Message1F("Step", config.stepping, DEC);
  Message1F("Abs", config.abs_mode, DEC);
}

