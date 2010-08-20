
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
  Serial.print("Steps:"); 
  Serial.print(config.steps_inch.x,DEC);
  Serial.print(","); 
  Serial.print(config.steps_inch.y,DEC);
  Serial.print(","); 
  Serial.println(config.steps_inch.z,DEC);
  Serial.print("Feed:"); 
  Serial.print(config.max_feedrate.x,DEC);
  Serial.print(","); 
  Serial.print(config.max_feedrate.y,DEC);
  Serial.print(","); 
  Serial.println(config.max_feedrate.z,DEC);
  Serial.print("Current:"); 
  Serial.print(config.current.x,DEC);
  Serial.print(","); 
  Serial.print(config.current.y,DEC);
  Serial.print(","); 
  Serial.println(config.current.z,DEC);
  Serial.print("Stepping:"); 
  Serial.println(config.stepping,DEC);
  Serial.print("ABS:"); 
  Serial.println(config.abs_mode,DEC);
}

