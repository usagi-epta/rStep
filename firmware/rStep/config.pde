

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
	Serial.println("ERRCHECKSUM");
  }
}

void config_dump(void) {
  Message3F("SBI", config.steps_inch.x, config.steps_inch.y, config.steps_inch.z, DEC);
  Message3F("MFR", config.max_feedrate.x, config.max_feedrate.y, config.max_feedrate.z, DEC);
  Message3F("Cur", config.current.x, config.current.y, config.current.z, DEC);
  Message1F("Step", config.stepping, DEC);
  Message1F("Abs", config.abs_mode, DEC);
}

