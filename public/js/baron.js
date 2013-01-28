function Link(data) {
  this.id       = ko.observable(data.id);
  this.url      = ko.observable(data.url);
  this.poster   = ko.observable(data.poster);
  this.org      = ko.observable(data.org);
  this.position = ko.observable(data.position);
  this.description = ko.observable(data.description);
  this.dateCreated = ko.observable(data.dateCreated);
}