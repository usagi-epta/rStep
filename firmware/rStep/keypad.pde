/*

uint8_t COLUMN[3] = {16,14,17};
uint8_t ROW[4]    = {15,19,18,4};

void keypad_init() {
  int x;
  
  //keypad
  for (x=0;x<3;x++) {
    pinMode(COLUMN[x], OUTPUT);
    digitalWrite(COLUMN[x], HIGH);
  }
  for (x=0;x<4;x++) {
    pinMode(ROW[x], INPUT);
    digitalWrite(ROW[x], HIGH);
  }
}

uint16_t keypad_scan() {
  int x,y;
  uint16_t out = 0;
  for (x=0;x<3;x++) {
    digitalWrite(COLUMN[x], LOW);
    //scan 1st three rows
    for (y=0;y<4;y++) {
      if (digitalRead(ROW[y])==LOW) out |= _BV(y*3+x+1);
    }
    digitalWrite(COLUMN[x], HIGH);
  }
  return out;
}

*/
