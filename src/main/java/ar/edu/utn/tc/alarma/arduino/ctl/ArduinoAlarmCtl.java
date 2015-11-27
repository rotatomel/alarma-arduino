/*
 * Copyright 2015 UTN Facultad Regional Resistencia.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ar.edu.utn.tc.alarma.arduino.ctl;

import ar.edu.utn.tc.alarma.arduino.Serial;
import ar.edu.utn.tc.alarma.arduino.SerialException;
import ar.edu.utn.tc.alarma.arduino.view.MainControlPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.Timer;
import org.apache.commons.lang.StringUtils;

/**
 * Clase para controlar e interpretar las órdenes de ida y vuelta entre la aplicación y la placa arduino
 *
 * @author Rodrigo M. Tato Rothamel mailto:rotatomel@gmail.com
 */
public class ArduinoAlarmCtl implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Serial serial;
    private final Timer updateTimer;
    private final MainControlPanel panel;

    public ArduinoAlarmCtl(String port, MainControlPanel panel) throws SerialException {
        serial = new Serial(port);
        this.panel = panel;
        updateTimer = new Timer(33, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                timerLoop();
            }
        });  // redraw serial monitor at 30 Hz
        updateTimer.start();
    }

    public void dispose() throws IOException {
        serial.dispose();
    }

    /**
     * Este método se dispara cuando se produce un evento de monitoreo sobre la placa arduino
     */
    private void timerLoop() {
        String s = serial.consumeStringBuffer();
        if (StringUtils.isEmpty(s)) {
            return;
        }
        //System.out.println(s);
        String[] commands = StringUtils.split(s, "\n\r");
        for (String cmd : commands) {
            if (!StringUtils.isEmpty(cmd)) {
                parseCommand(cmd);
            }
        }
        //Interpretar los mensajes que llegan de arduino
    }

    /**
     * Método para encender o apagar las luces
     *
     * @param component el componente de luz que se quiere apagar o prender
     * @param on si es true la luz se prende y se apaga si es false
     */
    public void switchLigth(Components component, boolean on) {
        int command = 0;
        command = on ? component.getIdComponent() : component.getIdComponent() * -1;
        serial.write(command);
    }

    /**
     * Método que interpreta un comando recibido desde la placa arduino
     *
     * @param cmd el comando en string que se debe interpretar
     */
    private void parseCommand(String cmd) {
        try {
            Integer idSensor = Integer.parseInt(StringUtils.trim(cmd));
            if (Math.abs(idSensor) >= 8 && Math.abs(idSensor) <= 15) {
                panel.switchSensors(Math.abs(idSensor), idSensor > 0);
            }
            if (idSensor == 22) {
                panel.triggerAlarm();
            }
        } catch (NumberFormatException ex) {
            //Ignore command
        }

    }
}
