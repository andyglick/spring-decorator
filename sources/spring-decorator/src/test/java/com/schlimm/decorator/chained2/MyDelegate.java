package com.schlimm.decorator.chained2;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class MyDelegate implements MyServiceInterface {

	public void setDelegateClass(MyDelegate delegate) {
		// TODO Auto-generated method stub
		
	}

	public MyDelegate getDelegateClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDelegateInterface(MyServiceInterface serviceDelegate) {
		// TODO Auto-generated method stub
		
	}

	public MyServiceInterface getDelegateInterface() {
		// TODO Auto-generated method stub
		return null;
	}

}
