function Link(data) {
  this.id       = ko.observable(data.id);
  this.url      = ko.observable(data.url);
  this.poster   = ko.observable(data.poster);
  this.org      = ko.observable(data.org);
  this.position = ko.observable(data.position);
  this.description = ko.observable(data.description);
  this.dateCreated = ko.observable(data.dateCreated);
}

function LinkViewModel() {
  var self = this;
  self.links = ko.observableArray([]);

  console.log("ASD");
  $.getJSON("/api/link")
  .done(function(data) {
    var mappedLinks = $.map(data, function(item) { return new Link(item) });
    self.links(mappedLinks);
  })
  .fail(function() { console.log("XXX Failed to retrieve links!") });
}