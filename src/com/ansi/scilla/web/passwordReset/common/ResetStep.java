package com.ansi.scilla.web.passwordReset.common;

public enum ResetStep {
	VERIFY,   // we've got a valid user asking for a reset
	CONFIRM,  // we've got a confirmation code
	GO,	      // we've got a new password to insert into the DB
	;
}
