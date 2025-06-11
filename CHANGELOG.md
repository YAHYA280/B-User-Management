## [1.0.0]

### Added

- add role endpoint logic
- get role endpoint logic
- Implemented GET API endpoints for retrieving admin, parent, and child information.
- Add GET /roles endpoint with pagination, sorting, and searching
- Add SOFT DELETE endpoint to remove a user (admin, parent, child)
- ADD user roles to keycloak
- Add DELETE endpoint to remove a role
- Block Admin, Parent, Child endpoint
- Reactivate Admin, Parent, Child endpoint
- List Accounts endpoint with pagination, sorting, and searching
- Suspend Admin, Parent, Child endpoint
- Delete/Suspend/Block/Reactivate children by parent id
- Add Get parent by gatewayCustomerId fro stripe use.
- Add update Role endpoint
- Add isAssigned role endpoint
- Add PUT endpoint to update a user by ID
- Add PUT endpount to update gatewayCustomerID for the parent
- Add UserStatusHistory to Users
- Add userType to baseUserResponse
- Add API login for BO et AP
- Add API for getting parent ids using parent name
- ADD api get current admin/parent authenticated

### CHANGED

- refactor(user):replace list of addresses with primary/secondary address
- prevent adding a child to a deleted parent
- count total users and statuses before applying filters

### Remove

- Removed automatic blocking of children when parent is blocked; now handled by front after admin confirmation
- Removed automatic soft deleting of children when parent is soft deleted; now handled by front after admin confirmation
