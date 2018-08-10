package com.team2073.common.controllers.mapping;

import edu.wpi.first.wpilibj.GenericHID;

import java.util.List;

/**
 * @author pbriggs
 */
public abstract class AbstractControllerMapping {

    ButtonMapping mapping;

    public void mapButtons(GenericHID controller, List<ButtonMapping> mappings) {

    }
}
