#ifndef __MESSAGES_H_
#define __MESSAGES_H_

#define Message1(msg,value) 	Serial.print("MSG "); \
				Serial.print(msg);\
				Serial.print('(');\
				Serial.print(value);\
				Serial.println(')')
#define Message1F(msg,value,format) 	Serial.print("MSG ");\
					Serial.print(msg);\
					Serial.print('(');\
					Serial.print(value, format);\
					Serial.println(')')
#define Message3F(msg,x,y,z,format) 	Serial.print("MSG ");\
					Serial.print(msg);\
					Serial.print('(');\
					Serial.print(x, format);\
					Serial.print(',');\
					Serial.print(y, format);\
					Serial.print(',');\
					Serial.print(z, format);\
					Serial.println(')')
#define MessageCoordinates(x,y,z) Message3F("Coord",x,y,z,5)

#endif

