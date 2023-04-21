var system = require('system');
var page = require('webpage').create();
page.viewportSize = { width: 1280, height: 768 };
page.paperSize = {
  format: 'A4',
  orientation: 'portrait',
  margin: '0.1cm'
}

var args = system.args;
page.open(args[2], function () {
    page.render(args[1]);
  phantom.exit();
});
