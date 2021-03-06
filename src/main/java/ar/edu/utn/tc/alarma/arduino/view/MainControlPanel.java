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
package ar.edu.utn.tc.alarma.arduino.view;

import ar.edu.utn.tc.alarma.arduino.SerialException;
import ar.edu.utn.tc.alarma.arduino.SerialPortList;
import ar.edu.utn.tc.alarma.arduino.ctl.ArduinoAlarmCtl;
import ar.edu.utn.tc.alarma.arduino.ctl.Components;
import ar.edu.utn.tc.alarma.arduino.mail.SendMail;
import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JToggleButton;

/**
 * Clase que representa el panel de control general para gobernar los componentes de la placa de manera gŕafica
 *
 * @author Rodrigo M. Tato Rothamel mailto:rotatomel@gmail.com
 */
public class MainControlPanel extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;
    private static final String OFF = "OFF";
    private static final String ON = "ON";
    private static final String ERROR_INICIAR_SISTEMA = "Error al tratar de iniciar el sistema de alarma";
    private static final String ERROR_FINALIZAR_SISTEMA = "Error al tratar de finalizar el sistema de alarma";

    private static final Logger LOG = Logger.getLogger(MainControlPanel.class.getName());
    private boolean systemEngaged = false;
    private boolean alarmTriggered = false;
    private boolean alarmEnabled = false;
    private Components sensorViolated = null;

    private ArduinoAlarmCtl ctl;

    /**
     * Creates new form MainControlPanel
     */
    public MainControlPanel() {
        initComponents();
        initControls();
    }

    private void initControls() {
        portsCombo.setModel(new DefaultComboBoxModel(SerialPortList.getPortNames()));
        allowUserInteraction(false);
    }

    /**
     * Habilita los comandos para que los usuarios puedan interactuar de manera gráfica
     *
     * @param allow si es true permite la operación y false no lo permite
     */
    private void allowUserInteraction(boolean allow) {
        bedroom2LightButton.setEnabled(allow);
        bedroom3LightButton.setEnabled(allow);
        bedroom4LightButton.setEnabled(allow);
        hallLightButton.setEnabled(allow);
        bathroom3LightButton.setEnabled(allow);
        stairsLightButton.setEnabled(allow);
        //Menues
        switchAlarmaMenuItem.setEnabled(allow);
        allLightsMenuItem.setEnabled(allow);

        //Sensors
        hallMovementSensorButton.setEnabled(allow);
        hallWindow1SensorButton.setEnabled(allow);
        hallWindow2SensorButton.setEnabled(allow);
        mainDoorSensorButton.setEnabled(allow);
        bedroom2WindowSensorButton.setEnabled(allow);
        bedroom3WindowSensorButton.setEnabled(allow);
        bedroom4WindowSensorButton.setEnabled(allow);

    }

    private void switchLight(javax.swing.JToggleButton button, Components light) {
        if (button.isSelected()) {
            button.setText(OFF);
            ctl.switchLigth(light, true);
            LOG.log(Level.INFO, String.format("Light %s is now %s", light, ON));
        } else {
            button.setText(ON);
            ctl.switchLigth(light, false);
            LOG.log(Level.INFO, String.format("Light %s is now %s", light, OFF));
        }
    }

    private void switchAlarm(javax.swing.JCheckBoxMenuItem button, Components alarm) {

        //Verificar que no haya sensores disparados
        //Enviar mail
        if (button.isSelected()) {
            if (checkSensors()) {
                button.setText("Desactivar alarma");
                ctl.switchLigth(alarm, true);
                alarmEnabled = true;
                LOG.log(Level.INFO, String.format("El componente %s está ahora %s", alarm, ON));
            } else {
                button.setSelected(false);
                showMessageDialog(this, "No se puede activar la alarma hasta que todas las áreas estén seguras!",
                        "Alarma", JOptionPane.WARNING_MESSAGE);

            }
        } else {
            button.setText("Activar alarma");
            ctl.switchLigth(alarm, false);
            alarmEnabled = false;
            LOG.log(Level.INFO, String.format("El componente %s está ahora %s", alarm, OFF));
            if (alarmTriggered) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        SendMail.sendMail("rotatomel@gmail.com", "La alarma se ha disparado y ha sido desactivada!",
                                "ALARMA");
                    }
                }).start();
                alarmTriggered = false;
            }

        }

    }

    private boolean checkSensors() {
        return bedroom3WindowSensorButton.isSelected()
                && bedroom2WindowSensorButton.isSelected()
                && bedroom4WindowSensorButton.isSelected()
                && hallWindow1SensorButton.isSelected()
                && hallWindow2SensorButton.isSelected()
                && mainDoorSensorButton.isSelected()
                && hallMovementSensorButton.isSelected();
    }

    /**
     * Activa o desactiva los sensores según hayan sido disparados en la placa
     *
     * @param idSensor el número de identificación del sensor a activar o desactivar
     * @param on si es true activa el sensor y si es false lo desactiva
     */
    public void switchSensors(int idSensor, boolean on) {
        Components component = Components.getComponentById(idSensor);
        if (alarmEnabled) {
            sensorViolated = component;
        }
        switch (component) {
            case BEDROOM3_WINDOW_SENSOR:
                switchSensorButtton(bedroom3WindowSensorButton, on);
                break;

            case BEDROOM2_WINDOW_SENSOR:
                switchSensorButtton(bedroom2WindowSensorButton, on);
                break;
            case BEDROOM4_WINDOW_SENSOR:
                switchSensorButtton(bedroom4WindowSensorButton, on);
                break;
            case HALL_WINDOW1_SENSOR:
                switchSensorButtton(hallWindow1SensorButton, on);
                break;
            case HALL_WINDOW2_SENSOR:
                switchSensorButtton(hallWindow2SensorButton, on);
                break;
            case MAIN_DOOR_SENSOR:
                switchSensorButtton(mainDoorSensorButton, on);
                break;
            case HALL_MOVEMENT_SENSOR:
                switchSensorButtton(hallMovementSensorButton, on);
                break;
        }
        LOG.log(Level.INFO, String.format("El estado del sensor %s ahora es %s", component, on ? "habilitado" : "deshabilitado"));
    }

    private void switchSensorButtton(JToggleButton button, boolean on) {
        button.setSelected(on);
        button.setBackground(Color.GRAY);
        if (alarmEnabled) {
            button.setBackground(Color.RED);
        }
    }

    /**
     * Se invoca cuando se dispara la alarma
     */
    public void triggerAlarm() {
        String message = String.format("Te están choriando hermano!\nSe ha detectado un intruso en: %s", sensorViolated);
        new Thread(new Runnable() {

            @Override
            public void run() {
                SendMail.sendMail("rotatomel@gmail.com", message, "ALARMA");
            }
        }).start();
        showMessageDialog(this, message, "Alarma", JOptionPane.WARNING_MESSAGE);
        alarmTriggered = true;
    }

    /**
     * Pone el sistema en estado de control activo y permite que el usuario interactúe. Inicia la comunicación con la
     * placa arduino.
     */
    private void engageSystem() {
        try {
            ctl = new ArduinoAlarmCtl(String.valueOf(portsCombo.getSelectedItem()), this);
            systemEngaged = true;
            allowUserInteraction(true);
            iniciarPanelMenuItem.setText("Apagar");
        } catch (SerialException ex) {
            LOG.log(Level.SEVERE, null, ex);
            iniciarPanelMenuItem.setText("Iniciar");
            iniciarPanelMenuItem.setSelected(false);
            showMessageDialog(this, ERROR_INICIAR_SISTEMA, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Pone el sistema en estado de control inactivo y no permite la interacción del usuario. Corta la comunicación con
     * la placa arduino.
     */
    private void disEngageSytem() {
        if (ctl != null && systemEngaged) {
            try {
                ctl.dispose();
                ctl = null;
                systemEngaged = false;
                iniciarPanelMenuItem.setText("Iniciar");
                allowUserInteraction(false);

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                iniciarPanelMenuItem.setText("Apagar");
                iniciarPanelMenuItem.setSelected(true);
                showMessageDialog(this, ERROR_FINALIZAR_SISTEMA, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        portsCombo = new javax.swing.JComboBox();
        bedroom3LightButton = new javax.swing.JToggleButton();
        hallLightButton = new javax.swing.JToggleButton();
        bedroom2LightButton = new javax.swing.JToggleButton();
        bedroom4LightButton = new javax.swing.JToggleButton();
        bathroom3LightButton = new javax.swing.JToggleButton();
        stairsLightButton = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        mainDoorSensorButton = new javax.swing.JToggleButton();
        bedroom4WindowSensorButton = new javax.swing.JToggleButton();
        hallWindow1SensorButton = new javax.swing.JToggleButton();
        hallWindow2SensorButton = new javax.swing.JToggleButton();
        bedroom2WindowSensorButton = new javax.swing.JToggleButton();
        bedroom3WindowSensorButton = new javax.swing.JToggleButton();
        hallMovementSensorButton = new javax.swing.JToggleButton();
        planoLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        iniciarPanelMenuItem = new javax.swing.JCheckBoxMenuItem();
        allLightsMenuItem = new javax.swing.JCheckBoxMenuItem();
        switchAlarmaMenuItem = new javax.swing.JCheckBoxMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Panel de control de alarma centralizada");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        portsCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(portsCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 6, 180, -1));

        bedroom3LightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_off.png"))); // NOI18N
        bedroom3LightButton.setText("ON");
        bedroom3LightButton.setToolTipText("");
        bedroom3LightButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_on.png"))); // NOI18N
        bedroom3LightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bedroom3LightButtonActionPerformed(evt);
            }
        });
        getContentPane().add(bedroom3LightButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 260, -1, -1));

        hallLightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_off.png"))); // NOI18N
        hallLightButton.setText("ON");
        hallLightButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_on.png"))); // NOI18N
        hallLightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hallLightButtonActionPerformed(evt);
            }
        });
        getContentPane().add(hallLightButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 360, -1, -1));

        bedroom2LightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_off.png"))); // NOI18N
        bedroom2LightButton.setText("ON");
        bedroom2LightButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_on.png"))); // NOI18N
        bedroom2LightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bedroom2LightButtonActionPerformed(evt);
            }
        });
        getContentPane().add(bedroom2LightButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 310, -1, -1));

        bedroom4LightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_off.png"))); // NOI18N
        bedroom4LightButton.setText("ON");
        bedroom4LightButton.setToolTipText("");
        bedroom4LightButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_on.png"))); // NOI18N
        bedroom4LightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bedroom4LightButtonActionPerformed(evt);
            }
        });
        getContentPane().add(bedroom4LightButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 320, -1, -1));

        bathroom3LightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_off.png"))); // NOI18N
        bathroom3LightButton.setText("ON");
        bathroom3LightButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_on.png"))); // NOI18N
        bathroom3LightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bathroom3LightButtonActionPerformed(evt);
            }
        });
        getContentPane().add(bathroom3LightButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, -1, -1));

        stairsLightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_off.png"))); // NOI18N
        stairsLightButton.setText("ON");
        stairsLightButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb_on.png"))); // NOI18N
        stairsLightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stairsLightButtonActionPerformed(evt);
            }
        });
        getContentPane().add(stairsLightButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 460, -1, -1));

        jLabel2.setText("Port:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 11, -1, -1));

        mainDoorSensorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlock.png"))); // NOI18N
        mainDoorSensorButton.setToolTipText("Sensor de la puerta principal");
        mainDoorSensorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
        mainDoorSensorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainDoorSensorButtonActionPerformed(evt);
            }
        });
        getContentPane().add(mainDoorSensorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 240, -1, -1));

        bedroom4WindowSensorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlock.png"))); // NOI18N
        bedroom4WindowSensorButton.setToolTipText("Sensor de ventana de habitación 4");
        bedroom4WindowSensorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
        bedroom4WindowSensorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bedroom4WindowSensorButtonActionPerformed(evt);
            }
        });
        getContentPane().add(bedroom4WindowSensorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 360, -1, -1));

        hallWindow1SensorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlock.png"))); // NOI18N
        hallWindow1SensorButton.setToolTipText("Sensor de ventana 1 de living");
        hallWindow1SensorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
        hallWindow1SensorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hallWindow1SensorButtonActionPerformed(evt);
            }
        });
        getContentPane().add(hallWindow1SensorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 520, -1, -1));

        hallWindow2SensorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlock.png"))); // NOI18N
        hallWindow2SensorButton.setToolTipText("Sensor de ventana 2 de living");
        hallWindow2SensorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
        hallWindow2SensorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hallWindow2SensorButtonActionPerformed(evt);
            }
        });
        getContentPane().add(hallWindow2SensorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 520, -1, -1));

        bedroom2WindowSensorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlock.png"))); // NOI18N
        bedroom2WindowSensorButton.setToolTipText("Sensor de ventana de habitación 2");
        bedroom2WindowSensorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
        bedroom2WindowSensorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bedroom2WindowSensorButtonActionPerformed(evt);
            }
        });
        getContentPane().add(bedroom2WindowSensorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, -1, -1));

        bedroom3WindowSensorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlock.png"))); // NOI18N
        bedroom3WindowSensorButton.setToolTipText("Sensor de ventana de habitación 1");
        bedroom3WindowSensorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
        bedroom3WindowSensorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bedroom3WindowSensorButtonActionPerformed(evt);
            }
        });
        getContentPane().add(bedroom3WindowSensorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        hallMovementSensorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlock.png"))); // NOI18N
        hallMovementSensorButton.setSelected(true);
        hallMovementSensorButton.setToolTipText("Sensor de movimiento del living");
        hallMovementSensorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock.png"))); // NOI18N
        hallMovementSensorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hallMovementSensorButtonActionPerformed(evt);
            }
        });
        getContentPane().add(hallMovementSensorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 320, -1, -1));

        planoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Plano.png"))); // NOI18N
        getContentPane().add(planoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, -1));

        fileMenu.setMnemonic('f');
        fileMenu.setText("Panel de control");

        iniciarPanelMenuItem.setMnemonic('i');
        iniciarPanelMenuItem.setText("Iniciar");
        iniciarPanelMenuItem.setToolTipText("Inicia el panel de control");
        iniciarPanelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniciarPanelMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(iniciarPanelMenuItem);

        allLightsMenuItem.setText("On/Off Luces");
        allLightsMenuItem.setEnabled(false);
        allLightsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allLightsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(allLightsMenuItem);

        switchAlarmaMenuItem.setText("Activar alarma");
        switchAlarmaMenuItem.setToolTipText("Arma la alarma si es posible");
        switchAlarmaMenuItem.setEnabled(false);
        switchAlarmaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchAlarmaMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(switchAlarmaMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setMnemonic('y');
        helpMenu.setText("Ayuda");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contenidos");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("Acerca de...");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void bedroom3LightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bedroom3LightButtonActionPerformed
        switchLight((JToggleButton) evt.getSource(), Components.BEDROOM3_LIGHT);
    }//GEN-LAST:event_bedroom3LightButtonActionPerformed

    private void bedroom2LightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bedroom2LightButtonActionPerformed
        switchLight((JToggleButton) evt.getSource(), Components.BEDROOM2_LIGHT);
    }//GEN-LAST:event_bedroom2LightButtonActionPerformed

    private void bathroom3LightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bathroom3LightButtonActionPerformed
        switchLight((JToggleButton) evt.getSource(), Components.BATHROOM3_LIGHT);
    }//GEN-LAST:event_bathroom3LightButtonActionPerformed

    private void hallLightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hallLightButtonActionPerformed
        switchLight((JToggleButton) evt.getSource(), Components.HALL_LIGHT);
    }//GEN-LAST:event_hallLightButtonActionPerformed

    private void bedroom4LightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bedroom4LightButtonActionPerformed
        switchLight((JToggleButton) evt.getSource(), Components.BEDROOM4_LIGHT);
    }//GEN-LAST:event_bedroom4LightButtonActionPerformed

    private void iniciarPanelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniciarPanelMenuItemActionPerformed
        if (iniciarPanelMenuItem.isSelected()) {
            engageSystem();
        } else {
            disEngageSytem();
        }
    }//GEN-LAST:event_iniciarPanelMenuItemActionPerformed

    private void stairsLightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stairsLightButtonActionPerformed
        switchLight((JToggleButton) evt.getSource(), Components.STAIR_LIGHT);
    }//GEN-LAST:event_stairsLightButtonActionPerformed

    private void switchAlarmaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchAlarmaMenuItemActionPerformed
        switchAlarm((JCheckBoxMenuItem) evt.getSource(), Components.ALARM);
    }//GEN-LAST:event_switchAlarmaMenuItemActionPerformed

    private void hallMovementSensorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hallMovementSensorButtonActionPerformed
        avoidClick((JToggleButton) evt.getSource());
    }//GEN-LAST:event_hallMovementSensorButtonActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        showMessageDialog(this, "Sistema de control de alarma desarrollado por alumnos de 4to año de ISI para Teoría de Control.\nAutores:\nMotter, Pamela\nSilva Beker, Eliana\nSilva, Omar\nTato Rothamel, Rodrigo", "Acerca de",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void allLightsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allLightsMenuItemActionPerformed
        stairsLightButton.setSelected(!stairsLightButton.isSelected());
        switchLight(stairsLightButton, Components.STAIR_LIGHT);

        bedroom3LightButton.setSelected(!bedroom3LightButton.isSelected());
        switchLight(bedroom3LightButton, Components.BEDROOM3_LIGHT);

        bedroom2LightButton.setSelected(!bedroom2LightButton.isSelected());
        switchLight(bedroom2LightButton, Components.BEDROOM2_LIGHT);

        bathroom3LightButton.setSelected(!bathroom3LightButton.isSelected());
        switchLight(bathroom3LightButton, Components.BATHROOM3_LIGHT);

        bedroom4LightButton.setSelected(!bedroom4LightButton.isSelected());
        switchLight(bedroom4LightButton, Components.BEDROOM4_LIGHT);

        hallLightButton.setSelected(!hallLightButton.isSelected());
        switchLight(hallLightButton, Components.HALL_LIGHT);

    }//GEN-LAST:event_allLightsMenuItemActionPerformed

    private void mainDoorSensorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainDoorSensorButtonActionPerformed
        avoidClick((JToggleButton) evt.getSource());
    }//GEN-LAST:event_mainDoorSensorButtonActionPerformed

    private void bedroom4WindowSensorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bedroom4WindowSensorButtonActionPerformed
        avoidClick((JToggleButton) evt.getSource());
    }//GEN-LAST:event_bedroom4WindowSensorButtonActionPerformed

    private void bedroom2WindowSensorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bedroom2WindowSensorButtonActionPerformed
        avoidClick((JToggleButton) evt.getSource());
    }//GEN-LAST:event_bedroom2WindowSensorButtonActionPerformed

    private void bedroom3WindowSensorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bedroom3WindowSensorButtonActionPerformed
        avoidClick((JToggleButton) evt.getSource());
    }//GEN-LAST:event_bedroom3WindowSensorButtonActionPerformed

    private void hallWindow1SensorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hallWindow1SensorButtonActionPerformed
        avoidClick((JToggleButton) evt.getSource());
    }//GEN-LAST:event_hallWindow1SensorButtonActionPerformed

    private void hallWindow2SensorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hallWindow2SensorButtonActionPerformed
        avoidClick((JToggleButton) evt.getSource());
    }//GEN-LAST:event_hallWindow2SensorButtonActionPerformed

    /**
     * Evita que el usuario haga clic en un sensor para desactivarlo/activarlo
     *
     * @param btn el boton
     */
    private void avoidClick(JToggleButton btn) {
        btn.setSelected(!btn.isSelected());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JCheckBoxMenuItem allLightsMenuItem;
    private javax.swing.JToggleButton bathroom3LightButton;
    private javax.swing.JToggleButton bedroom2LightButton;
    private javax.swing.JToggleButton bedroom2WindowSensorButton;
    private javax.swing.JToggleButton bedroom3LightButton;
    private javax.swing.JToggleButton bedroom3WindowSensorButton;
    private javax.swing.JToggleButton bedroom4LightButton;
    private javax.swing.JToggleButton bedroom4WindowSensorButton;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToggleButton hallLightButton;
    private javax.swing.JToggleButton hallMovementSensorButton;
    private javax.swing.JToggleButton hallWindow1SensorButton;
    private javax.swing.JToggleButton hallWindow2SensorButton;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JCheckBoxMenuItem iniciarPanelMenuItem;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToggleButton mainDoorSensorButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel planoLabel;
    private javax.swing.JComboBox portsCombo;
    private javax.swing.JToggleButton stairsLightButton;
    private javax.swing.JCheckBoxMenuItem switchAlarmaMenuItem;
    // End of variables declaration//GEN-END:variables

}
