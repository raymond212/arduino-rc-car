#include <SoftwareSerial.h>
SoftwareSerial BT(10, 11);

#define LEFT_F 4
#define LEFT_B 5
#define LEFT_E 3
#define RIGHT_F 6
#define RIGHT_B 8
#define RIGHT_E 9

void setup() {
  Serial.begin(9600);
  BT.begin(9600);
  pinMode(LEFT_F, OUTPUT);
  pinMode(LEFT_B, OUTPUT);
  pinMode(RIGHT_F, OUTPUT);
  pinMode(RIGHT_B, OUTPUT);

  
  digitalWrite(LEFT_F, LOW);
  digitalWrite(LEFT_B, LOW);
  digitalWrite(RIGHT_F, LOW);
  digitalWrite(RIGHT_B, LOW);  
}

void loop() {
  if (BT.available() == 3) {
    int buf[3];
    for (int i = 0; i < 3; i++) {
      buf[i] = BT.read();
      if (buf[i] >= 128) {
        buf[i] -= 256;
      }
      Serial.print(buf[i]);
      Serial.print(" ");
    }
    Serial.println();
    int power, forward, left;
    power = buf[0];
    forward = buf[1];
    left = buf[2];
    if (power == 0) {
      motorWrite(0, 0);
    } else {
      int x_power = round((forward / abs(forward)) * (forward * forward) / power);
      int y_power = round((left / abs(left)) * (left * left) / power);
      int motor_left = x_power;
      int motor_right = x_power;
      if (x_power >= 0) {
        if (y_power >= 0) {
          motor_right += y_power;
        } else {
          motor_left -= y_power;
        }
      } else {
        if (y_power >= 0) {
          motor_right -= y_power;
        } else {
          motor_left += y_power;
        }
      }
      motorWrite(motor_left, motor_right);      
    }
  }
}

void motorWrite(int left, int right) {
  int a = 0;
  int b = 0;
  int c = 0;
  int d = 0;
  if (left >= 0) {
    a = HIGH;
    b = LOW;
  } else {
    a = LOW;
    b = HIGH;
  }
  if (right >= 0) {
    c = HIGH;
    d = LOW;
  } else {
    c = LOW;
    d = HIGH;
  }
  left = map(abs(left), 0, 100, 0, 255);
  right = map(abs(right), 0, 100, 0, 255);
  
  digitalWrite(LEFT_F, a);
  digitalWrite(LEFT_B, b);
  digitalWrite(RIGHT_F, c);
  digitalWrite(RIGHT_B, d);  
  
  analogWrite(LEFT_E, left);
  analogWrite(RIGHT_E, right);
}
