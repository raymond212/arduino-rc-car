#include <SoftwareSerial.h>
SoftwareSerial BT(10, 11);

#define LEFT_F 3
#define LEFT_B 5
#define RIGHT_F 6
#define RIGHT_B 9
//
//int lastPower = 0;
//int lastForward = 0;
//int lastLeft = 0;

void setup() {
  Serial.begin(9600);
  BT.begin(9600);

  analogWrite(LEFT_F, 0);
  analogWrite(LEFT_B, 0);
  analogWrite(RIGHT_F, 0);
  analogWrite(RIGHT_B, 0);  
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
//    int power, forward, left;
//    power = buf[0];
//    forward = buf[1];
//    left = buf[2];
//    if (power == 0) {
//      motorWrite(0, 0);
//    } else {
//      int x_power = round((forward / abs(forward)) * (forward * forward) / power);
//      int y_power = round((left / abs(left)) * (left * left) / power);
//      int motor_left = x_power;
//      int motor_right = x_power;
//      if (x_power >= 0) {
//        if (y_power >= 0) {
//          motor_right += y_power;
//        } else {
//          motor_left -= y_power;
//        }
//      } else {
//        if (y_power >= 0) {
//          motor_right -= y_power;
//        } else {
//          motor_left += y_power;
//        }
//      }
//      motorWrite(motor_left, motor_right);      
//    }
  }
}

void motorWrite(int left, int right) {
  int a = 0;
  int b = 0;
  int c = 0;
  int d = 0;
  if (left >= 0) {
    a = left;
  } else {
    b = -left;
  }
  if (right >= 0) {
    c = right;
  } else {
    d = -right;
  }
  a = map(a, 0, 100, 0, 255);
  b = map(b, 0, 100, 0, 255);
  c = map(c, 0, 100, 0, 255);
  d = map(d, 0, 100, 0, 255);
  analogWrite(LEFT_F, a);
  analogWrite(LEFT_B, b);
  analogWrite(RIGHT_F, c);
  analogWrite(RIGHT_B, d);  
}
