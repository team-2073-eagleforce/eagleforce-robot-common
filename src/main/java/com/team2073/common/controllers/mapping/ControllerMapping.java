package com.team2073.common.controllers.mapping;

import com.team2073.common.controllers.mapping.ControllerMappingRegistry.MappingType;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * @author pbriggs
 */
public interface ControllerMapping {
    void mapButtons(GenericHID controller, MappingType mappingType);
}
