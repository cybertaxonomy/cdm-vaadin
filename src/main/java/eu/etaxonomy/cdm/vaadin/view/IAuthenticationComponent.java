package eu.etaxonomy.cdm.vaadin.view;

import eu.etaxonomy.cdm.common.URI;

public interface IAuthenticationComponent {

	public interface AuthenticationComponentListener {

        /**
         * @param uri
         * @param context
         * @param userName
         * @param password
         * @return
         */
        boolean login(URI uri, String context, String userName, String password);
    }
    public void addListener(AuthenticationComponentListener listener);


}
