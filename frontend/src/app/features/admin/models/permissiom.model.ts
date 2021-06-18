export class PermissionRequest {
  user: number;
  role: number;
  permitted: boolean;

  constructor(userId: number, roleId: number, isPremitted: boolean) {
    this.user = userId;
    this.role = roleId;
    this.permitted = isPremitted;
  }

}
