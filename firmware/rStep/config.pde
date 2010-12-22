

// Units in curve section
#define CURVE_SECTION_INCHES 0.019685
#define CURVE_SECTION_MM 0.5


void config_save(void) {
  uint16_t i;
  uint8_t checksum = 0;
  uint8_t *ptr = (uint8_t*)(&config);
  for (i=0; i<sizeof(struct config_t); i++) {
    EEPROM.write(i,ptr[i]);
    checksum += ptr[i];
  }
  EEPROM.write(i+1,checksum);
}

void config_read(void) {
  uint16_t i;
  uint8_t checksum = 0;
  uint8_t *ptr = (uint8_t*)(&config);
  for (i=0; i<sizeof(struct config_t); i++) {
    ptr[i] = EEPROM.read(i);
    checksum += ptr[i];
  }
  if (EEPROM.read(i+1) != checksum) {
	//XXX add more error checking code here
	Serial.println("ERR CHECKSUM");
  }
}

float val2current(uint8_t val) {
  float out;
  out = (float)(((uint16_t)val)<<1);
  out = (out * 2.0) / (float)0x100; 
  return out;
}
  
  
void config_dump(void) {
  Message3("SBI", config.steps_inch.x, config.steps_inch.y, config.steps_inch.z,DEC);
  Message3("MFR", config.max_feedrate.x, config.max_feedrate.y, config.max_feedrate.z,DEC);
  Message3F("Cur", val2current(config.current.x), val2current(config.current.y), val2current(config.current.z));
  Message1F("Step", config.stepping, DEC);
  Message1F("Abs", config.abs_mode, DEC);
}

