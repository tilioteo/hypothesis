/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Group;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.model.UrlConsumer;
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

	public static class StartFeaturedTestEvent {
		private Pack pack;

		public StartFeaturedTestEvent(Pack pack) {
			this.pack = pack;
		}

		public Pack getPack() {
			return pack;
		}
	}

	public static class StartLegacyTestEvent {
		private Pack pack;
		private UrlConsumer urlConsumer;

		public StartLegacyTestEvent(Pack pack, UrlConsumer urlConsumer) {
			this.pack = pack;
			this.urlConsumer = urlConsumer;
		}

		public Pack getPack() {
			return pack;
		}

		public UrlConsumer getUrlConsumer() {
			return urlConsumer;
		}
	}

	public static final class CloseOpenWindowsEvent {
	}

	public static final class ProfileUpdatedEvent {
	}

	public static final class UserAddedEvent {
		private final User user;

		public UserAddedEvent(final User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static final class UserSelectionChangedEvent {
	}

	public static final class GroupUsersChangedEvent {
		private final Group group;

		public GroupUsersChangedEvent(final Group group) {
			this.group = group;
		}

		public Group getGroup() {
			return group;
		}
	}

	public static final class GroupAddedEvent {
		private final Group group;

		public GroupAddedEvent(final Group group) {
			this.group = group;
		}

		public Group getGroup() {
			return group;
		}
	}

	public static final class GroupSelectionChangedEvent {
	}

	public static final class UserGroupsChangedEvent {
		private final User user;

		public UserGroupsChangedEvent(final User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static final class TestSelectionChangedEvent {
	}

}
