/**
   Aplicación para controlar una alarma hogareña controlada por puerto serie a través de una PC
   UTN Facultad Regional Resistencia - 2015

   @autor Motter Pemela
   @autor Silva Beker Eliana
   @autor Silva Omar
   @autor Tato Rothamel Rodrigo
*/
//Estado de la alarma
boolean alarmaOn = false;
//Alarma disparada
boolean alarmTriggered = false;

//Sensores
boolean bedroom3Window = false;
boolean bedroom2Window = false;
boolean hallWindow1 = false;
boolean hallWindow2 = false;
boolean frontDoor = false;
boolean bedroom4Window = false;
boolean hallMovement = false;


const int BEDROOM_3_LIGHT = 2;
const int BEDROOM_2_LIGHT = 3;
const int BEDROOM_4_LIGHT = 6;
const int HALL_LIGHT = 4;
const int BATHROOM_3_LIGHT = 5;
const int STAIRS_LIGHT = 7;

const int BEDROOM_3_WINDOW_SENSOR = 8;
const int BEDROOM_2_WINDOW_SENSOR = 9;
const int HALL_WINDOW_1_SENSOR = 10;
const int HALL_WINDOW_2_SENSOR = 11;
const int BEDROOM_4_WINDOW_SENSOR = 12;
const int MAIN_DOOR_SENSOR = A0;
const int ALARM_LED = 13;
const int HALL_MOVEMENT_SENSOR = A1;
const int ALARM_TRIGGERED_CODE_EVENT = 22;//Código de disparo de alarma

// the setup function runs once when you press reset or power the board

void setup() {
  // initialize digital pin 13 as an output.
  pinMode(ALARM_LED, OUTPUT);
  //Lights
  pinMode(BEDROOM_3_LIGHT, OUTPUT);
  pinMode(BEDROOM_2_LIGHT, OUTPUT);
  pinMode(BEDROOM_4_LIGHT, OUTPUT);
  pinMode(HALL_LIGHT, OUTPUT);
  pinMode(BATHROOM_3_LIGHT, OUTPUT);
  pinMode(STAIRS_LIGHT, OUTPUT);
  //Sensors
  pinMode(BEDROOM_3_WINDOW_SENSOR, INPUT);
  pinMode(BEDROOM_2_WINDOW_SENSOR, INPUT);
  pinMode(HALL_WINDOW_1_SENSOR, INPUT);
  pinMode(HALL_WINDOW_2_SENSOR, INPUT);
  pinMode(BEDROOM_4_WINDOW_SENSOR, INPUT);
  pinMode(MAIN_DOOR_SENSOR, INPUT);
  pinMode(HALL_MOVEMENT_SENSOR, INPUT);


  digitalWrite(ALARM_LED, LOW);

  Serial.begin(9600);
}

// the loop function runs over and over again forever
void loop() {

  int command = Serial.read();

  if (command != -1) {
    switchLights(command);
    switchAlarm(command);
  }
  checkSensors();

  delay(100);

  // wait for a second
}

//Prende o apaga las luces
//Recibe como parámetro el número de pin al que está conectada la luz
//Si el número es positivo prende la luz y si es negativo la apaga
//Como no se pueden recibir números negativos se le resta 256 para
//obtener el número de pin.
void switchLights(int light) {
  if ((light >= 2 && light <= 7) || (light >= 248 && light <= 254)) {
    //Serial.println(light);
    if (light > 248) {
      digitalWrite(abs(light - 256), LOW);
    } else {
      digitalWrite(abs(light), HIGH);
    }
  }
}


void switchAlarm(int light) {
  //Verificar que ningun sensor este activado para poder activar alarma

  if (light == 13 || light == 243) {
    //Serial.println(light);
    if (light == 243) {
      digitalWrite(abs(light - 256), LOW);
      alarmaOn = false;

    } else {
      if (!hallMovement && bedroom3Window && bedroom2Window && hallWindow1 && hallWindow2 && frontDoor
          && bedroom4Window) {
        digitalWrite(abs(light), HIGH);
        alarmaOn = true;
      }
    }
    alarmTriggered = false;//Vuelvo el estado de activación de alarma a su estado normal
  }
}

void checkSensors() {

  //Comienzo el chequeo de cada uno de los sensores
  if (digitalRead(BEDROOM_3_WINDOW_SENSOR) == HIGH) {
    if (bedroom3Window) {
      bedroom3Window = false;
      //Write event to console
      Serial.println(-BEDROOM_3_WINDOW_SENSOR);
      if (alarmaOn) {
        alarmTriggered = true;
      }
    } else {
      bedroom3Window = true;
      Serial.println(BEDROOM_3_WINDOW_SENSOR);
    }
  }

  if (digitalRead(BEDROOM_2_WINDOW_SENSOR) == HIGH) {
    if (bedroom2Window) {
      bedroom2Window = false;
      //Write event to console
      Serial.println(-BEDROOM_2_WINDOW_SENSOR);
      if (alarmaOn) {
        alarmTriggered = true;
      }
    } else {
      bedroom2Window = true;
      Serial.println(BEDROOM_2_WINDOW_SENSOR);
    }
  }

  if (digitalRead(HALL_WINDOW_1_SENSOR) == HIGH) {
    if (hallWindow1) {
      hallWindow1 = false;
      //Write event to console
      Serial.println(-HALL_WINDOW_1_SENSOR);
      if (alarmaOn) {
        alarmTriggered = true;
      }
    } else {
      hallWindow1 = true;
      Serial.println(HALL_WINDOW_1_SENSOR);
    }
  }

  if (digitalRead(HALL_WINDOW_2_SENSOR) == HIGH) {
    if (hallWindow2) {
      hallWindow2 = false;
      //Write event to console
      Serial.println(-HALL_WINDOW_2_SENSOR);
      if (alarmaOn) {
        alarmTriggered = true;
      }
    } else {
      hallWindow2 = true;
      Serial.println(HALL_WINDOW_2_SENSOR);
    }
  }

  if (digitalRead(BEDROOM_4_WINDOW_SENSOR) == HIGH) {
    if (bedroom4Window) {
      bedroom4Window = false;
      //Write event to console
      Serial.println(-BEDROOM_4_WINDOW_SENSOR);
      if (alarmaOn) {
        alarmTriggered = true;
      }
    } else {
      bedroom4Window = true;
      Serial.println(BEDROOM_4_WINDOW_SENSOR);
    }
  }

  if (digitalRead(MAIN_DOOR_SENSOR) == HIGH) {
    if (frontDoor) {
      frontDoor = false;
      //Write event to console
      Serial.println(-MAIN_DOOR_SENSOR);
      if (alarmaOn) {
        alarmTriggered = true;
      }
    } else {
      frontDoor = true;
      Serial.println(MAIN_DOOR_SENSOR);
    }
  }

  //Valor menor al 10% del maximo de la entrada del potenciometro
  //En este caso la lógica se invierte y se pasa como negativo para cuando
  //no hay movimiento
  if (analogRead(HALL_MOVEMENT_SENSOR) < 67) {
    //Se detectan movimientos
    if (!hallMovement) {
      Serial.println(-HALL_MOVEMENT_SENSOR);
      hallMovement = true;
      if (alarmaOn) {
        alarmTriggered = true;
      }
    }
  } else {
    //Ya no hay movimientos
    if (hallMovement) {

      Serial.println(HALL_MOVEMENT_SENSOR);
      hallMovement = false;

    }
  }
  //Finalizo el chequeo de cada uno de los sensores

  //Si la alarama se dispara aviso a la interfaz gráfica y ejecuto el método
  if (alarmTriggered) {
    Serial.println(ALARM_TRIGGERED_CODE_EVENT);
    alarmTriggered = false;
    alarm();
  }
}

//Este método es llamado cuando la alarma se dispara
// y hace que las luces de la casa destellen con semiperiodo de 250ms durante 1 minuto
void alarm() {
  long time = 0;
  while (time < 120) {
    for (int i = 2 ; i <= 7; i++) {
      switchLights(i);
    }
    delay(250);
    for (int j = 248 ; j <= 254; j++) {
      switchLights(j);
    }
    delay(250);
    time++;
  }

}

