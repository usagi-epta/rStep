#define MSP4351_CS 14
#define mcp4351_end() SPIend()

#define SCK 13
#define MOSI 11
#define MISO 12
#define SS 10
#define SPI_CLOCK_DIV2 0x04
#define SPI_MODE0 0x00
#define SPI_MODE1 0x04
#define SPI_MODE2 0x08
#define SPI_MODE3 0x0C
#define SPI_MODE_MASK 0x0C  // CPOL = bit 3, CPHA = bit 2 on SPCR
#define SPI_CLOCK_MASK 0x03  // SPR1 = bit 1, SPR0 = bit 0 on SPCR
#define SPI_2XCLOCK_MASK 0x01  // SPI2X = bit 0 on SPS

uint8_t SPItransfer(uint8_t x) {
  SPDR = x;
  while (!(SPSR & _BV(SPIF)));
  return SPDR;
}

/***************************************************/
/***************************************************/


void mcp4351_init(void) {
  //configure pins
  pinMode(MSP4351_CS, OUTPUT);
  pinMode(SCK, OUTPUT);
  pinMode(MOSI, OUTPUT);
  pinMode(SS, OUTPUT); //just to be safe
  pinMode(MISO, INPUT);
  
  digitalWrite(SCK, LOW);
  digitalWrite(MOSI, LOW);
  digitalWrite(MISO, LOW);

  SPCR = _BV(MSTR) | _BV(SPE) ;

  //restore values
  if (! (mcp4351_setWiper(CHAN_X, (uint16_t)(config.current.x<<1)) &&
         mcp4351_setWiper(CHAN_Y, (uint16_t)(config.current.y<<1)) &&
         mcp4351_setWiper(CHAN_Z, (uint16_t)(config.current.z<<1)))) {
           Serial.println("INIT_ERR");
           while(1); //lock up device as we can't set the currents
  }
}

uint8_t mcp4351_setWiper(uint8_t wiper, uint16_t value) {
  uint8_t a,b;
  uint8_t addrs[4] = {0<<4,1<<4,6<<4,7<<4};
  value &= 0x1FF;
  digitalWrite(MSP4351_CS, LOW);
  a=SPItransfer(addrs[wiper] | (value>>8));
  b=SPItransfer(value & 0xFF);
  digitalWrite(MSP4351_CS, HIGH);
  if (a!=0xFF || b!=0xFF) return false;
  return true;
}

#define MAX_CURRENT 2.0
uint8_t setCurrent(uint8_t channel, float current) {
  if (current > MAX_CURRENT) return false;
  uint16_t val = (0x100*(current/MAX_CURRENT));
  return (mcp4351_setWiper(channel, val)) ?  (val>>1):0; 
}

