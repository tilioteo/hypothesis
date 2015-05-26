/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.io.Serializable;

import com.tilioteo.hypothesis.entity.Group;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.model.UrlConsumer;
import com.tilioteo.hypothesis.ui.view.HypothesisViewType;
import com.tilioteo.hypothesis.ui.view.ProcessView;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
public abstract class HypothesisEvent implements Serializable {

	public static final class UserLoginRequestedEvent implements Serializable {
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

	public static class GuestAccessRequestedEvent implements Serializable {
	}

	public static class InvalidLoginEvent implements Serializable {
	}

	public static class InvalidUserPermissionEvent implements Serializable {
	}

	public static class UserLoggedOutEvent implements Serializable {
	}

	public static final class PostViewChangeEvent implements Serializable {
		private final HypothesisViewType view;

		public PostViewChangeEvent(final HypothesisViewType view) {
			this.view = view;
		}

		public HypothesisViewType getView() {
			return view;
		}
	}

	public static class StartFeaturedTestEvent implements Serializable {
		private User user;
		private Pack pack;

		public StartFeaturedTestEvent(User user, Pack pack) {
			this.user = user;
			this.pack = pack;
		}

		public Pack getPack() {
			return pack;
		}
		
		public User getUser() {
			return user;
		}
	}

	public static class StartLegacyTestEvent implements Serializable {
		private User user;
		private Pack pack;
		private UrlConsumer urlConsumer;

		public StartLegacyTestEvent(User user, Pack pack, UrlConsumer urlConsumer) {
			this.user = user;
			this.pack = pack;
			this.urlConsumer = urlConsumer;
		}

		public Pack getPack() {
			return pack;
		}

		public UrlConsumer getUrlConsumer() {
			return urlConsumer;
		}

		public User getUser() {
			return user;
		}
	}

	public static final class CloseOpenWindowsEvent implements Serializable {
	}

	public static final class ProfileUpdatedEvent implements Serializable {
	}

	public static final class UserAddedEvent implements Serializable {
		private final User user;

		public UserAddedEvent(final User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static final class UserSelectionChangedEvent implements Serializable {
	}

	public static final class GroupUsersChangedEvent implements Serializable {
		private final Group group;

		public GroupUsersChangedEvent(final Group group) {
			this.group = group;
		}

		public Group getGroup() {
			return group;
		}
	}

	public static final class GroupAddedEvent implements Serializable {
		private final Group group;

		public GroupAddedEvent(final Group group) {
			this.group = group;
		}

		public Group getGroup() {
			return group;
		}
	}

	public static final class GroupSelectionChangedEvent implements Serializable {
	}

	public static final class UserGroupsChangedEvent implements Serializable {
		private final User user;

		public UserGroupsChangedEvent(final User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static final class UserPacksChangedEvent implements Serializable {
		private final User user;

		public UserPacksChangedEvent(final User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

		public static final class PackSelectionChangedEvent implements Serializable {
	}

	public static final class ExportFinishedEvent implements Serializable {
		private final boolean canceled;
		
		public ExportFinishedEvent(final boolean canceled) {
			this.canceled = canceled;
		}
		
		public boolean isCanceled() {
			return canceled;
		}
	}

	public static final class ExportErrorEvent implements Serializable {
	}

	public static final class ExportProgressEvent implements Serializable {
		private final float progress;
		
		public ExportProgressEvent(final float progress) {
			this.progress = progress;
		}
		
		public float getProgress() {
			return progress;
		}
	}
	
	public static final class ProcessViewEndEvent {
		private final ProcessView view;
		
		public ProcessViewEndEvent(final ProcessView view) {
			this.view = view;
		}
		
		public ProcessView getView() {
			return view;
		}
	}
}
