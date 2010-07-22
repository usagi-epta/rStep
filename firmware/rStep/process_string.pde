


//steps per inch or mm
float _units[3] = {
  X_STEPS_PER_MM,Y_STEPS_PER_MM,Z_STEPS_PER_MM};
float curve_section = CURVE_SECTION_MM;

//our feedrate variables.
float feedrate = 0.0;
long feedrate_micros = 0;



void setXYZ(FloatPoint *fp) {
  fp->x = (command_exists('X')) ? (getValue('X') + ((abs_mode) ? 0 : xaxis->current_units)) :   
  xaxis->current_units;
  fp->y = (command_exists('Y')) ? (getValue('Y') + ((abs_mode) ? 0 : yaxis->current_units)) :   
  yaxis->current_units;
  fp->z = (command_exists('Z')) ? (getValue('Z') + ((abs_mode) ? 0 : zaxis->current_units)) :   
  zaxis->current_units;
}



//Read the string and execute instructions
void process_string(uint8_t  *instruction) {
  uint8_t code;
  uint16_t k;
  float temp;
  //command commands = NULL;
  FloatPoint fp;


  //the character / means delete block... used for comments and stuff.
  if (instruction[0] == '/') 	{
    Serial.println("ok");
    return;
  }

  enable_steppers();
  purge_commands(); //clear old commands
  parse_commands(instruction); //create linked list of arguments
  if (command_exists('G')) {
    code = getValue('G');

    switch(code) {
    case 0: //Rapid Motion
      setXYZ(&fp);
      set_target(&fp);
      r_move(0); //fast motion in all axis
      break;
    case 1: //Coordinated Motion
      setXYZ(&fp);
      set_target(&fp);
      if (command_exists('F')) _feedrate = getValue('F'); //feedrate persists till changed.
      r_move( _feedrate );
      break;
    case 2://Clockwise arc
    case 3://Counterclockwise arc
      FloatPoint cent;
      float angleA, angleB, angle, radius, length, aX, aY, bX, bY;

      //Set fp Values
      setXYZ(&fp);
      // Centre coordinates are always relative
      cent.x = xaxis->current_units + getValue('I');
      cent.y = yaxis->current_units + getValue('J');

      aX = (xaxis->current_units - cent.x);
      aY = (yaxis->current_units - cent.y);
      bX = (fp.x - cent.x);
      bY = (fp.y - cent.y);

      if (code == 2) { // Clockwise
        angleA = atan2(bY, bX);
        angleB = atan2(aY, aX);
      } 
      else { // Counterclockwise
        angleA = atan2(aY, aX);
        angleB = atan2(bY, bX);
      }

      // Make sure angleB is always greater than angleA
      // and if not add 2PI so that it is (this also takes
      // care of the special case of angleA == angleB,
      // ie we want a complete circle)
      if (angleB <= angleA) angleB += 2 * M_PI;
      angle = angleB - angleA;

      radius = sqrt(aX * aX + aY * aY);
      length = radius * angle;
      int steps, s, step;
      steps = (int) ceil(length / curve_section);

      FloatPoint newPoint;
      for (s = 1; s <= steps; s++) {
        step = (code == 3) ? s : steps - s; // Work backwards for CW
        newPoint.x = cent.x + radius * cos(angleA + angle * ((float) step / steps));
        newPoint.y = cent.y + radius * sin(angleA + angle * ((float) step / steps));
        newPoint.z = zaxis->current_units;
        set_target(&newPoint);

        // Need to calculate rate for each section of curve
        feedrate_micros = (feedrate > 0) ? feedrate : getMaxFeedrate();

        // Make step
        r_move(feedrate_micros);
      }

      break;
    case 4: //Dwell
      //delay((int)getValue('P'));
      break;
    case 20: //Inches for Units
      _units[0] = X_STEPS_PER_INCH;
      _units[1] = Y_STEPS_PER_INCH;
      _units[2] = Z_STEPS_PER_INCH;
      curve_section = CURVE_SECTION_INCHES;
      calculate_deltas();
      break;
    case 21: //mm for Units
      _units[0] = X_STEPS_PER_MM;
      _units[1] = Y_STEPS_PER_MM;
      _units[2] = Z_STEPS_PER_MM; 
      curve_section = CURVE_SECTION_MM;
      calculate_deltas();
      break;
    case 28: //go home.
      set_target(&zeros);
      r_move(getMaxFeedrate());
      break;
    case 30://go home via an intermediate point.
      //Set Target
      setXYZ(&fp);
      set_target(&fp);
      //go there.
      r_move(getMaxFeedrate());
      //go home.
      set_target(&zeros);
      r_move(getMaxFeedrate());
      break;
    case 81: // drilling operation
      temp = zaxis->current_units;
      //Move only in the XY direction
      setXYZ(&fp);
      set_target(&fp);
      zaxis->target_units = temp;
      calculate_deltas();
      r_move(getMaxFeedrate());
      //Drill DOWN
      zaxis->target_units = getValue('Z') + ((abs_mode) ? 0 : zaxis->current_units);
      calculate_deltas();
      r_move(getMaxFeedrate());
      //Drill UP
      zaxis->target_units = temp;
      calculate_deltas();
      r_move(getMaxFeedrate());
    case 90://Absolute Positioning
      abs_mode = true;
      break;
    case 91://Incremental Positioning
      abs_mode = false;
      break;
    case 92://Set as home
      set_position(&zeros);
      break;
    case 93://Inverse Time Feed Mode
      break;  //TODO: add this 
    case 94://Feed per Minute Mode
      break;  //TODO: add this
    default:
      Serial.print("huh? G");
      Serial.println(code,DEC);
    }
  }
  if (command_exists('M')) {
    code = getValue('M');
    switch(code) {
    case 3: // turn on motor
    case 4:
      motor_on();
      break;
    case 5: // turn off motor
      motor_off();
      break;
    case 82:
      DDRC |= _BV(1);
      PORTC &= ~_BV(1);
      DDRC &= ~_BV(0);
      PORTC |= _BV(0);
      // setup initial position
      for (int i=0; i<20; i++) {
        k=0;
        PORTB |= _BV(5); //go down
        while(PINC & _BV(0)) {
          PORTB |= _BV(2);
          delayMicroseconds(1);
          PORTB &= ~_BV(2);
          delayMicroseconds(200);
          k++;
        }
        //print result for this point
        Serial.println(k,DEC);
        PORTB &= ~_BV(5);  //move up to origin        
        while (k--) {
          PORTB |= _BV(2);
          delayMicroseconds(1);
          PORTB &= ~_BV(2);
          delayMicroseconds(12.5*stepping);
        }
      }
      break;
    case 81:
      DDRC |= _BV(1);
      PORTC &= ~_BV(1);
      DDRC &= ~_BV(0);
      PORTC |= _BV(0);
      while(1) {
        if (PINC & _BV(0)) Serial.println("high");
        else Serial.println("low");
      }
      break;
    case 80: //plot out surface of milling area
      DDRC |= _BV(1);
      PORTC &= ~_BV(1);
      DDRC &= ~_BV(0);
      PORTC |= _BV(0);
      // setup initial position
      fp.x = 0;
      fp.y = 0;
      fp.z = 0;
      set_target(&fp);
      set_position(&fp);
      r_move(0);
      for (int i=0; i<160; i+=2) {
        for (float j=0; j<75; j+=2) {
          fp.x=i;
          fp.y=j;
          fp.z=0;
          set_target( &fp );
          r_move( 0 );
          k=0;
          PORTB &= ~(_BV(5)); //go down
          while(PINC & _BV(0)) {
            PORTB |= _BV(2);
            delayMicroseconds(1);
            PORTB &= ~_BV(2);
            delayMicroseconds(200);
            k++;
          }
          //print result for this point
          Serial.print(i,DEC);
          Serial.print(",");
          Serial.print(j,DEC);
          Serial.print(",");
          Serial.println(k,DEC);
          PORTB |= _BV(5);  //move up to origin        
          while (k--) {
            PORTB |= _BV(2);
            delayMicroseconds(1);
            PORTB &= ~_BV(2);
            delayMicroseconds(200);
          }
        }
      }
      break;
    case 90: //plot out surface of milling area
      DDRC |= _BV(1);
      PORTC &= ~_BV(1);
      DDRC &= ~_BV(0);
      PORTC |= _BV(0);
      // setup initial position
      fp.x = 0;
      fp.y = 135;
      fp.z = 0;
      set_target(&fp);
      set_position(&fp);
      r_move(0);
      for (int i=0; i<1; i++) {
        for (float j=135; j!=0; j-=.25) {
          fp.x=i;
          fp.y=j;
          fp.z=0;
          set_target( &fp );
          r_move( 0 );
          k=0;
          PORTB |= _BV(5); //go down
          while(PINC & _BV(0)) {
            PORTB |= _BV(2);
            delayMicroseconds(1);
            PORTB &= ~_BV(2);
            delayMicroseconds(200);
            k++;
          }
          //print result for this point
          Serial.print(i,DEC);
          Serial.print(",");
          Serial.print(j,DEC);
          Serial.print(",");
          Serial.println(k,DEC);
          PORTB &= ~_BV(5);  //move up to origin        
          while (k--) {
            PORTB |= _BV(2);
            delayMicroseconds(1);
            PORTB &= ~_BV(2);
            delayMicroseconds(200);
          }
        }
      }
      break;
    case 98: //M98 Find Z0 where it is one step from touching.
      DDRC |= _BV(1);
      PORTC &= ~_BV(1);
      DDRC &= ~_BV(0);
      PORTC |= _BV(0);
      PORTB |= _BV(5);
      while(PINC & _BV(0)) {
        PORTB |= _BV(2);
        delayMicroseconds(1);
        PORTB &= ~_BV(2);
        delayMicroseconds(200);
      }
      break;
    case 99: //M99 S{1,2,4,8,16} -- set stepping mode
      if (command_exists('S')) {
        code = getValue('S');
        if (code == 1 || code == 2 || code == 4 || code == 8 || code == 16) {
          stepping = code;
          setStep(stepping);
          break;
        }
      }
    default:
      Serial.print("huh? M");
      Serial.println(code,DEC);
    }
  }
  Serial.println("ok");//tell our host we're done.
}
















