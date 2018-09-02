package com.team2073.common.simulation;

import com.team2073.common.simulation.SimulationConstants.Motors.Bag;
import com.team2073.common.simulation.SimulationConstants.Motors.Cim;
import com.team2073.common.simulation.SimulationConstants.Motors.MiniCim;
import com.team2073.common.simulation.SimulationConstants.Motors.Pro;

/**
 * @author pbriggs
 */
public class SimulationConstants {

    /**
     * Units are in terms of RPM, amps, watts, and inch pounds, unless otherwise specified.
     *
     * kv units are rps per volt
     * kt units are inch pounds per amp
     */
    public enum MotorType {
        PRO(Pro.MOTOR_KV, Pro.MOTOR_KT, Pro.RESISTANCE),
        BAG(Bag.MOTOR_KV, Bag.MOTOR_KT, Bag.RESISTANCE),
        CIM(Cim.MOTOR_KV, Cim.MOTOR_KT, Cim.RESISTANCE),
        MINI_CIM(MiniCim.MOTOR_KV, MiniCim.MOTOR_KT, MiniCim.RESISTANCE);
        
        public final double velocityConstant;
        public final double torqueConstant;
        public final double motorResistance;

        MotorType(double velocityConstant, double torqueConstant, double motorResistance) {
            this.velocityConstant = velocityConstant;
            this.torqueConstant = torqueConstant;
            this.motorResistance = motorResistance;
        }
    }

    public abstract class Motors {

        public abstract class Pro {
            public static final double FREE_SPEED_RPM = 18730.;
            public static final double FREE_CURRENT = .7;
            public static final double MAXIMUM_POWER = 347;
            public static final double STALL_TORQUE = 6.28;
            public static final double STALL_CURRENT = 134;
            public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
            public static final double RESISTANCE = 12 / STALL_CURRENT;
            public static final double MOTOR_KV = ((FREE_SPEED_RPM / 60) * Math.PI * 2)
                    / (12 - RESISTANCE * FREE_CURRENT);
        }

        public abstract class Bag {
            public static final double FREE_SPEED_RPM = 13180;
            public static final double FREE_CURRENT = 1.8;
            public static final double MAXIMUM_POWER = 149;
            public static final double STALL_TORQUE = 3.81;
            public static final double STALL_CURRENT = 53;
            public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
            public static final double RESISTANCE = 12 / STALL_CURRENT;
            public static final double MOTOR_KV = ((FREE_SPEED_RPM / 60) * Math.PI * 2)
                    / (12 - RESISTANCE * FREE_CURRENT);
        }

        public abstract class Cim {
            public static final double FREE_SPEED_RPM = 5330;
            public static final double FREE_CURRENT = /* 2.7 */1.17;
            public static final double MAXIMUM_POWER = 337;
            public static final double STALL_TORQUE = 21.33;
            public static final double STALL_CURRENT = 131;
            public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
            public static final double RESISTANCE = 12 / STALL_CURRENT;
            public static final double MOTOR_KV = ((FREE_SPEED_RPM / 60) * Math.PI * 2)
                    / (12 - RESISTANCE * FREE_CURRENT);
        }

        public abstract class MiniCim {
            public static final double FREE_SPEED_RPM = 5840;
            public static final double FREE_CURRENT = 3;
            public static final double MAXIMUM_POWER = 215;
            public static final double STALL_TORQUE = 12.48;
            public static final double STALL_CURRENT = 89;
            public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
            public static final double RESISTANCE = 12 / STALL_CURRENT;
            public static final double MOTOR_KV = ((FREE_SPEED_RPM / 60) * Math.PI * 2)
                    / (12 - RESISTANCE * FREE_CURRENT);
        }

    }

}
