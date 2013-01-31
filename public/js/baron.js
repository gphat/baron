function processJsonError(resp) {
  var data = JSON.parse(resp)
  if(typeof data !== "undefined") {
    for(var k in data) {
      if(data.hasOwnProperty(k)) {
        console.log("#field-" + k);
        $("#field-" + k).addClass("error");
      }
    }
  }
}

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

  $.getJSON("/api/link")
  .done(function(data) {
    var mappedLinks = $.map(data, function(item) { return new Link(item) });
    self.links(mappedLinks);
  })
  .fail(function() { console.log("XXX Failed to retrieve links!") });
}

function AddLinkViewModel() {
  var self = this;
  self.url = ko.observable("");
  self.position = ko.observable();
  self.org = ko.observable();
  self.description = ko.observable("");

  self.doSubmit = function () {
    console.log($("#add-link").serialize());
    $.ajax({
      type: "POST",
      url: "/api/link",
      // contentType: "application/json; charset=utf-8",
      dataType: "jsonp",
      data: $("#add-link").serialize()
    })
      .success(function(data) {
        console.log(data);
        $('#addLink').modal('hide')
      })
      .error(function(e) {
        processJsonError(e.responseText);
      })
  }
}