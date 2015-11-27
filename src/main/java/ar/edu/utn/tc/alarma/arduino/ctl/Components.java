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

/**
 * Tipo enumerado para llevar el registro de los componentes que pueden controlarse
 *
 * @author Rodrigo M. Tato Rothamel mailto:rotatomel@gmail.com
 */
public enum Components {

    /**
     * Luz del dormitorio 2
     */
    BEDROOM2_LIGHT(3),
    /**
     * Luz del dormitorio 3
     */
    BEDROOM3_LIGHT(2),
    /**
     * Luz del dormitorio 4
     */
    BEDROOM4_LIGHT(6),
    /**
     * Luz del living
     */
    HALL_LIGHT(4),
    /**
     * Luz del baño
     */
    BATHROOM3_LIGHT(5),
    /**
     * Alarma
     */
    ALARM(13),
    /**
     * Luz de la escalera
     */
    STAIR_LIGHT(7),
    /**
     * Luz del dormitorio 3
     */
    BEDROOM3_WINDOW_SENSOR(8),
    /**
     * Sensor de la ventana del dormitorio 2
     */
    BEDROOM2_WINDOW_SENSOR(9),
    /**
     * Sensor de la ventana 1 del living
     */
    HALL_WINDOW1_SENSOR(10),
    /**
     * Sensor de la ventana 2 del living
     */
    HALL_WINDOW2_SENSOR(11),
    /**
     * Sensor de la ventana del dormitorio 4
     */
    BEDROOM4_WINDOW_SENSOR(12),
    /**
     * Sensor de la puerta principal
     */
    MAIN_DOOR_SENSOR(14),
    /**
     * Sensor de movimiento del living
     */
    HALL_MOVEMENT_SENSOR(15);

    private final int idComponent;

    private Components(int idComponent) {
        this.idComponent = idComponent;
    }

    /**
     * Retorna el id del componente actual
     *
     * @return
     */
    public int getIdComponent() {
        return idComponent;
    }

    /**
     * Devuelve el componente con el id pasado por parámetro o null si no se encuentra.
     *
     * @param id int para el id del componente a buscar
     * @return el componente con el id pasado por parámetro o null su no existe
     */
    public static Components getComponentById(int id) {
        for (Components c : Components.values()) {
            if (c.getIdComponent() == id) {
                return c;
            }
        }
        return null;
    }

}
