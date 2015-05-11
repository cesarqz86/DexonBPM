package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.os.AsyncTask;

import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class ServiceExecuter {

    public class ExecuteLoginService extends AsyncTask<LoginRequestDto, Void, LoginResponseDto>{

        private Context currentContext;

        public ExecuteLoginService (Context context)
        {
            this.currentContext = context;
        }

        @Override
        protected LoginResponseDto doInBackground(LoginRequestDto... params) {
            ILoginService loginService = LoginService.getInstance();
            return loginService.loginUser(this.currentContext, params[0]);
        }
    }

}
