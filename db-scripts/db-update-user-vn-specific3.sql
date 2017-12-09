ALTER TABLE public.tbl_user ADD COLUMN auto_disable boolean NOT NULL DEFAULT false;

UPDATE public.tbl_user
SET auto_disable=true
FROM (
	SELECT b.user_id
	FROM tbl_role a,
		(SELECT user_id, count(role_id) FROM public.tbl_user_role GROUP BY user_id HAVING count(role_id)=1) b,
		public.tbl_user_role c
	WHERE c.user_id=b.user_id AND c.role_id=a.id AND a.name='USER') r
WHERE r.user_id = tbl_user.id;
