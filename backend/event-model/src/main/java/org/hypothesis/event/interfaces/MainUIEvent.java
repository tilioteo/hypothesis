/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.interfaces;

import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.User;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public interface MainUIEvent extends HypothesisEvent {

	public static final class UserLoginRequestedEvent implements MainUIEvent {
		private final String userName;
		private final String password;

		public UserLoginRequestedEvent(final String userName, final String password) {
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

	public static class GuestAccessRequestedEvent implements MainUIEvent {
	}

	public static class InvalidLoginEvent implements MainUIEvent {
	}

	public static class InvalidUserPermissionEvent implements MainUIEvent {
	}

	public static class UserLoggedOutEvent implements MainUIEvent {
	}

	public static final class PostViewChangeEvent implements MainUIEvent {
		private final String viewName;

		public PostViewChangeEvent(final String viewName) {
			this.viewName = viewName;
		}

		public String getViewName() {
			return viewName;
		}
	}

	/*
	 * public static class MaskEvent implements MainUIEvent { }
	 * 
	 * public static class LegacyWindowClosedEvent implements MainUIEvent { }
	 */

	public static final class CloseOpenWindowsEvent implements MainUIEvent {
	}

	public static final class ProfileUpdatedEvent implements MainUIEvent {
	}

	public static final class UserAddedEvent implements MainUIEvent {
		private final User user;

		public UserAddedEvent(final User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static final class UserSelectionChangedEvent implements MainUIEvent {
	}

	public static final class GroupUsersChangedEvent implements MainUIEvent {
		private final Group group;

		public GroupUsersChangedEvent(final Group group) {
			this.group = group;
		}

		public Group getGroup() {
			return group;
		}
	}

	public static final class GroupAddedEvent implements MainUIEvent {
		private final Group group;

		public GroupAddedEvent(final Group group) {
			this.group = group;
		}

		public Group getGroup() {
			return group;
		}
	}

	public static final class GroupSelectionChangedEvent implements MainUIEvent {
	}

	public static final class UserGroupsChangedEvent implements MainUIEvent {
		private final User user;

		public UserGroupsChangedEvent(final User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static final class UserPacksChangedEvent implements MainUIEvent {
		private final User user;

		public UserPacksChangedEvent(final User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static final class PackSelectionChangedEvent implements MainUIEvent {
	}

	public static final class ExportFinishedEvent implements MainUIEvent {
		private final boolean canceled;

		public ExportFinishedEvent(final boolean canceled) {
			this.canceled = canceled;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static final class ExportErrorEvent implements MainUIEvent {
	}

	public static final class ExportProgressEvent implements MainUIEvent {
		private final float progress;

		public ExportProgressEvent(final float progress) {
			this.progress = progress;
		}

		public float getProgress() {
			return progress;
		}
	}

}
