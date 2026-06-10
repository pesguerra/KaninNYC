export class FrontendUser {
  constructor(user) {
    Object.assign(this, user);
    this.features = user.features ?? [];
  }

  hasRole(role) {
    return this.role === role;
  }

  isAdmin() {
    return this.hasRole("ADMIN");
  }

  hasFeature(feature) {
    return this.features.includes(feature);
  }
}
