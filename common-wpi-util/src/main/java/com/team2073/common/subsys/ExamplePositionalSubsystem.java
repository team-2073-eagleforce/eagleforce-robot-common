package com.team2073.common.subsys;

import ch.qos.logback.classic.spi.Configurator;
import com.team2073.common.subsys.ExampleAppConstants.DashboardKeys;
import com.team2073.common.subsys.ExampleAppConstants.Defaults;
import com.team2073.common.subsys.ExampleAppConstants.Shooter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
//import com.google.inject.Inject;
//import com.google.inject.name.Named;
import com.team2073.common.position.Position;
import com.team2073.common.position.PositionContainer;
import com.team2073.common.position.PositionConverter;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.speedcontrollers.EagleSRX;
import com.team2073.common.subsys.ExamplePositionalSubsystem.Angle;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ExamplePositionalSubsystem extends PositionalMechanismController<Angle> {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/** The angular position the hall effect sensor is placed at. */
//	The angle of the hall effect sensor on the robot changed so instead of changing 
//	where zero was (and everything that referenced this), we just changed this from 0 to 80
	public static final int ZERO_ANGLE = 90;
	
	public static abstract class TalonProfiles {
		public static final int PID = 0;
		public static final int MP = 1;
	}

	public static abstract class PivotAngles {
		private static final double DEFAULT_VARIANCE = 2;
		private static final double INTAKE = 25;
		private static final double ZERO = 0;
		private static final double FRONT_FLAT = 30;
		private static final double BACK_FLAT = 120;
		private static final double FRONT_SHOOT = 55;
		private static final double BACK_SHOOT = 120;
	}
	
	public enum Angle implements PositionContainer {
		INTAKE		(PivotAngles.INTAKE, 		PivotAngles.DEFAULT_VARIANCE),
		ZERO		(PivotAngles.ZERO, 			PivotAngles.DEFAULT_VARIANCE),
		FRONT_FLAT	(PivotAngles.FRONT_FLAT, 	PivotAngles.DEFAULT_VARIANCE),
		FRONT_SHOOT	(PivotAngles.FRONT_SHOOT,	PivotAngles.DEFAULT_VARIANCE),
		BACK_SHOOT	(PivotAngles.BACK_SHOOT, 	PivotAngles.DEFAULT_VARIANCE),
		BACK_FLAT	(PivotAngles.BACK_FLAT, 	PivotAngles.DEFAULT_VARIANCE),
		/** Special case to allow an 'illegal' starting position where the intakes are in but elevator is down. */
		// TODO: Is there any reason we can't have intakes in stow position to start?
		STARTING	(PivotAngles.INTAKE, 	PivotAngles.DEFAULT_VARIANCE);
		
		final Position position;

		private Angle(double midPoint, double variance) {
			position = new Position(midPoint, variance);
		}
		
		@Override
		public String toString() {
			return super.toString() + ": " + position.toString();
		}

		@Override
		public Position getPosition() {
			return position;
		}
	}

	// Constructor
	// ============================================================
//	@Inject
	public ExamplePositionalSubsystem(/*@Named("shooterPivotMotor")*/ EagleSRX pivotMotor, /*@Named("shooterPivotLimit")*/ DigitalInput shooterPivotLimit) {
		super(new PositionConverterImpl());
		logger.info("Initializing ShooterPivotSubsystem.");
		
//		this.conf = new Configurator(logger, pivotMotor);
//		this.io = new IOGateway(logger, pivotMotor, shooterPivotLimit);
//		this.mp = new MotionProfilingGateway(logger, pivotMotor);
//		this.dev = new Dev(logger, pivotMotor);
//		
//		conf.configEncoder();
//		conf.configOutput();
//		conf.configPIDProfileGains();
//		conf.setPIDGains();
		logger.info("Initializing ShooterPivotSubsystem complete.");
	}

	// Public querying
	// ============================================================
//	/**
//	 * Is the shooter pivot behind the elevator bar. Moving down in this position
//	 * will break the pivot.
//	 */
//	public boolean isPivotBack() {
//		return io.getAngularPosition() > Angle.FRONT_FLAT.upperBound + 10;
//	}
	
	/** Manages setting configuration on various components. Generally this is one time configuration
	 * or configuration only changed during development (such as through the smartdashboard). */
	private static class Configurator implements SmartDashboardAware {

		private final Logger logger;
		private final EagleSRX pivotMotor;
		
		private double pgain = Shooter.PIVOT_P_GAIN;
		private double igain = Shooter.PIVOT_I_GAIN;
		private double dgain = Shooter.PIVOT_D_GAIN;
		
		/** @see Configurator */
		public Configurator(Logger logger, EagleSRX pivotMotor) {
			this.logger = logger;
			this.pivotMotor = pivotMotor;
		}
		
		public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
			smartDashboardAwareRegistry.registerInstance(this);
		}

		@Override
		public void updateSmartDashboard() {
			SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_P_GAIN, Defaults.INTAKE_P_GAIN);
			SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_I_GAIN, Defaults.INTAKE_I_GAIN);
			SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_D_GAIN, Defaults.INTAKE_D_GAIN);
//			SmartDashboard.putString(Shooter.NAME + " Pivot State", state.toString());
//			SmartDashboard.putString(Shooter.NAME + " Pivot CurrentPosition", positionState.toString());
		}

		@Override
		public void readSmartDashboard() {
			pgain = SmartDashboard.getNumber(DashboardKeys.INTAKE_P_GAIN, Defaults.DRIVETRAIN_P_GAIN);
			igain = SmartDashboard.getNumber(DashboardKeys.INTAKE_I_GAIN, Defaults.DRIVETRAIN_I_GAIN);
			dgain = SmartDashboard.getNumber(DashboardKeys.INTAKE_D_GAIN, Defaults.DRIVETRAIN_D_GAIN);
		}

		private void configEncoder() {
			logger.info("Setting feedback sensor to [{}].", FeedbackDevice.QuadEncoder);
			pivotMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, TalonProfiles.PID, 0);
		}

		private void setPIDGains() {
			logger.info("Setting PID gains for profile [{}]. F/P/I/D: {}/{}/{}.", TalonProfiles.PID, Shooter.PIVOT_F_GAIN, pgain, igain, dgain);
			pivotMotor.config_kF(TalonProfiles.PID, Shooter.PIVOT_F_GAIN, 10);
			pivotMotor.config_kP(TalonProfiles.PID, pgain, 0);
			pivotMotor.config_kI(TalonProfiles.PID, igain, 0);
			pivotMotor.config_kD(TalonProfiles.PID, dgain, 0);
		}

		private void configPIDProfileGains() {
			int profile = TalonProfiles.MP;
			double pGain = Shooter.PIVOT_HOLD_P;
			double iGain = Shooter.PIVOT_HOLD_I;
			double dGain = Shooter.PIVOT_HOLD_D;
			
			logger.info("Setting PID gains for profile [{}]. P/I/D: {}/{}/{}.", profile, Shooter.PIVOT_F_GAIN, pGain, iGain, dGain);
			pivotMotor.config_kP(profile, pGain, 0);
			pivotMotor.config_kI(profile, iGain, 0);
			pivotMotor.config_kD(profile, dGain, 0);
		}
		
		private void configOutput() {
			logger.info("Configuring peak/nominal output.");
			pivotMotor.configPeakOutputForward(.8, 0);
			pivotMotor.configPeakOutputReverse(-.8, 0);
			pivotMotor.configNominalOutputForward(0, 0);
			pivotMotor.configNominalOutputReverse(0, 0);
			pivotMotor.setInverted(Shooter.PIVOT_DEFAULT_DIRECTION);
		}
		
	}
	
	public static class PositionConverterImpl implements PositionConverter {

		@Override
		public double asPosition(int tics) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int asTics(double position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String positionalUnit() {
			// TODO Auto-generated method stub
			return "";
		}
		
	}

	@Override
	public void updateSmartDashboard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readSmartDashboard() {
		// TODO Auto-generated method stub
		
	}


}
