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

    BEDROOM2_LIGHT(3),
    BEDROOM3_LIGHT(2),
    BEDROOM4_LIGHT(6),
    HALL_LIGHT(4),
    BATHROOM3_LIGHT(5),
    ALARM(13),
    STAIR_LIGHT(7),
    BEDROOM3_WINDOW_SENSOR(8),
    BEDROOM2_WINDOW_SENSOR(9),
    HALL_WINDOW1_SENSOR(10),
    HALL_WINDOW2_SENSOR(11),
    BEDROOM4_WINDOW_SENSOR(12),
    MAIN_DOOR_SENSOR(14),
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
