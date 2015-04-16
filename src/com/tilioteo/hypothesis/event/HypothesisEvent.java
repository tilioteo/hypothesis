/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.view.HypothesisViewType;

/**
 * @author kamil
 * 
 */
public abstract class HypothesisEvent {

	public static final class UserLoginRequestedEvent {
		private final String userName, password;

		public UserLoginRequestedEvent(final String userName,
				final String password) {
			this.userName = userName;
			this.password = password;
		}

		public String getUserName() {
			return userName;
		}

		public String getPassword() {
			return password;
		}
	}

	public static class GuestAccessRequestedEvent {
	}

	public static class InvalidLoginEvent {
	}

	public static class InvalidUserPermissionEvent {
	}

	public static class UserLoggedOutEvent {
	}

    public static final class PostViewChangeEvent {
        private final HypothesisViewType view;

        public PostViewChangeEvent(final HypothesisViewType view) {
            this.view = view;
        }

        public HypothesisViewType getView() {
            return view;
        }
    }

}
