

//str: token in the form of Xnnn
//old: head of object chain else null
//returns: head of object chain

struct command_t command_list[MAX_COMMANDS];
uint8_t commandLength = 0;

void addObj(uint8_t *str) {
  command c;
  if (commandLength == MAX_COMMANDS) {
     error("addObj FULL");
     return;
  }
  c = &command_list[commandLength++];
  c->type   = str[0];
  c->value  = strtod((const char*)&str[1], NULL);
}

void purge_commands() {
  commandLength = 0;
}


void parse_commands(uint8_t *str) {
  uint8_t *token;
  
  do {
    token = (uint8_t*)strtok((char*)str, " \t"); //split on spaces and tabs
    str = NULL;
    if (token) addObj(token);
  } while (token);
}


//returns zero if value does not exist.
double getValue(const char x) {
  int i;
  //find entry
  for (i=0; i<commandLength; i++) {
    if (command_list[i].type == x) break;
  } 
  //did we find or run out?
  if (i==commandLength) return 0;
  
  return command_list[i].value;
}


bool command_exists(const char x) {
  for (int i=0; i<commandLength; i++) {
    if (command_list[i].type == x) return 1;
  } 
  return 0;
}
