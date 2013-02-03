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

function Category(data) {
  this.name = ko.observable(data.name);
}

function Link(data) {
  this.id       = ko.observable(data.id);
  this.read     = ko.observable(data.read);
  this.url      = ko.observable(data.url);
  this.poster   = ko.observable(data.poster);
  this.category = ko.observable(data.category);
  this.position = ko.observable(data.position);
  this.description = ko.observable(data.description);
  this.dateCreated = ko.observable(data.dateCreated);
}

function LinkViewModel(category, categories, links) {
  var self = this;
  self.category = ko.observable(category);
  self.categories = ko.observableArray($.map(categories, function(item) { return new Category(item); }));
  self.links = ko.observableArray($.map(links, function(item) { return new Link(item); }));

  // $.getJSON("/api/category")
  // .done(function(data) {
  //   var categories = $.map(data, function(item) { return new Category(item); })
  //   self.categories(categories);
  // })
  // .fail(function() { console.log("XXX Failed to retrieve categories!") });

  self.changeCategory = function(category) {
    var catQuery = "";
    if(category !== null && typeof category !== "undefined") {
      self.category(category.name());
      catQuery = "?category=" + self.category();
      history.pushState(null, null, catQuery);
    } else {
      self.category(null);
      history.pushState(null, null, "/");
    }

    $.getJSON("/api/link/1" + catQuery)
    .done(function(data) {
      var mappedLinks = $.map(data, function(item) { return new Link(item); });
      self.links(mappedLinks);
    })
    .fail(function() { console.log("XXX Failed to retrieve links!") });
    return true;
  }

  self.doRead = function(link) {

    var action = "POST";
    if(!link.read()) {
      action = "DELETE"
    }
    $.ajax({
      type: action,
      url: "/api/link/" + link.id() + "/read/1",
      dataType: "jsonp",
    })
      .success(function(data) {
        var link = new Link(data);
      })
      .error(function(e) {
        console.log(e);
      })
    // We return true so ko stops the event, the replace of the link
    // in the success wil handle the checkbox.
    return true;
  }

  self.doSubmit = function () {
    $.ajax({
      type: "POST",
      url: "/api/link",
      dataType: "jsonp",
      data: $("#add-link").serialize()
    })
      .success(function(data) {
        $('#addLink').modal('hide')
      })
      .error(function(e) {
        processJsonError(e.responseText);
      })
      return false;
  }

  //self.changeCategory(null);
}
