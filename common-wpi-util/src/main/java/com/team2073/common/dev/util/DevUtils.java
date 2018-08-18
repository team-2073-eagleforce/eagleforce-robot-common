package com.team2073.common.dev.util;

/**
 * A set of utilities only really useful during dev/testing.
 *
 * @author Preston Briggs
 */
public class DevUtils {

	private static int prestonsTestCounter = 0;
	private static int prestonsDeathTrigger = 100;
	
	/**
	 * Call this over and over until it blows up and throws an exception.
	 * (Used for exception testing).
	 */
	public static void YOUGOBOOM() {
		if(prestonsTestCounter > prestonsDeathTrigger) {
			prestonsTestCounter = 0;
			throw new RuntimeException("\n" + BOOOOOOM);
		}
		else if(prestonsTestCounter > (prestonsDeathTrigger -3))
			System.out.println(DROP_THE_BIGGER_BOMB);
		else if(prestonsTestCounter > (prestonsDeathTrigger / 2))
			System.out.println(DROP_THE_BOMB);
		
		
		System.out.println("Counter: " + prestonsTestCounter);
		
		prestonsTestCounter++;
	}
	
	/**
	 * Throws a runtime exception for you.
	 */
	public static void YOUGOBOOMNOW() {
		throw new RuntimeException(BOOOOOOM);
	}
	
	public static final String BOOOOOOM = 
			"                               ________________\r\n" + 
			"                          ____/ (  (    )   )  \\___\r\n" + 
			"                         /( (  (  )   _    ))  )   )\\\r\n" + 
			"                       ((     (   )(    )  )   (   )  )\r\n" + 
			"                     ((/  ( _(   )   (   _) ) (  () )  )\r\n" + 
			"                    ( (  ( (_)   ((    (   )  .((_ ) .  )_\r\n" + 
			"                   ( (  )    (      (  )    )   ) . ) (   )\r\n" + 
			"                  (  (   (  (   ) (  _  ( _) ).  ) . ) ) ( )\r\n" + 
			"                  ( (  (   ) (  )   (  ))     ) _)(   )  )  )\r\n" + 
			"                 ( (  ( \\ ) (    (_  ( ) ( )  )   ) )  )) ( )\r\n" + 
			"                  (  (   (  (   (_ ( ) ( _    )  ) (  )  )   )\r\n" + 
			"                 ( (  ( (  (  )     (_  )  ) )  _)   ) _( ( )\r\n" + 
			"                  ((  (   )(    (     _    )   _) _(_ (  (_ )\r\n" + 
			"                   (_((__(_(__(( ( ( |  ) ) ) )_))__))_)___)\r\n" + 
			"                   ((__)        \\\\||lll|l||///          \\_))\r\n" + 
			"                            (   /(/ (  )  ) )\\   )\r\n" + 
			"                          (    ( ( ( | | ) ) )\\   )\r\n" + 
			"                           (   /(| / ( )) ) ) )) )\r\n" + 
			"                         (     ( ((((_(|)_)))))     )\r\n" + 
			"                          (      ||\\(|(|)|/||     )\r\n" + 
			"                        (        |(||(||)||||        )\r\n" + 
			"                          (     //|/l|||)|\\\\ \\     )\r\n" + 
			"                        (/ / //  /|//||||\\\\  \\ \\  \\ _)\r\n" + 
			"-------------------------------------------------------------------------------";
	
	
	private static final String DROP_THE_BOMB = 
			"          ,--.!,\r\n" + 
			"       __/   -*-\r\n" + 
			"     ,d08b.  '|`\r\n" + 
			"     0088MM     \r\n" + 
			"     `9MMP'   ";
	
	private static final String BOOM = 
			"          _ ._  _ , _ ._\r\n" + 
			"        (_ ' ( `  )_  .__)\r\n" + 
			"      ( (  (    )   `)  ) _)\r\n" + 
			"     (__ (_   (_ . _) _) ,__)\r\n" + 
			"         `~~`\\ ' . /`~~`\r\n" + 
			"              ;   ;\r\n" + 
			"              /   \\\r\n" + 
			"_____________/_ __ \\_____________";
	
	private static final String DROP_THE_BIGGER_BOMB = 
			"                        . . .                         \r\n" + 
			"                         \\|/                          \r\n" + 
			"                       `--+--'                        \r\n" + 
			"                         /|\\                          \r\n" + 
			"                        ' | '                         \r\n" + 
			"                          |                           \r\n" + 
			"                          |                           \r\n" + 
			"                      ,--'#`--.                       \r\n" + 
			"                      |#######|                       \r\n" + 
			"                   _.-'#######`-._                    \r\n" + 
			"                ,-'###############`-.                 \r\n" + 
			"              ,'#####################`,               \r\n" + 
			"             /#########################\\              \r\n" + 
			"            |###########################|             \r\n" + 
			"           |#############################|            \r\n" + 
			"           |#############################|            \r\n" + 
			"           |#############################|            \r\n" + 
			"           |#############################|            \r\n" + 
			"            |###########################|             \r\n" + 
			"             \\#########################/              \r\n" + 
			"              `.#####################,'               \r\n" + 
			"                `._###############_,'                 \r\n" + 
			"                   `--..#####..--'     ";
}
