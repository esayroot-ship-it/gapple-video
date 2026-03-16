const USER_KEY = 'video_user';

export interface UserInfo {
  id: number;
  username: string;
  nickname?: string;
  avatarUrl?: string;
}

export function getUser(): UserInfo | null {
  const userStr = sessionStorage.getItem(USER_KEY);
  return userStr ? JSON.parse(userStr) : null;
}

export function setUser(user: UserInfo): void {
  sessionStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function removeUser(): void {
  sessionStorage.removeItem(USER_KEY);
}

export function isLoggedIn(): boolean {
  return !!getUser();
}
