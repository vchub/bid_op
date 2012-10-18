package common;

import scala.None$;
import scala.Option;

public class ScalaLang {
	
	public static <T> Option<T> none() {
	    return (Option<T>) None$.MODULE$;
	}
}
