'use strict';

// # Globbing
// for performance reasons we're only matching one level down:
// 'test/spec/{,*/}*.js'
// If you want to recursively match all subfolders, use:
// 'test/spec/**/*.js'

module.exports = function(grunt) {
  grunt.file.defaultEncoding = 'utf8';
  // Configurable paths
  var config = {
    app: 'assets',
    dist: 'static_resources',
    project: 'shaidan'
  };
  //声明要被替换的文件;
  var files = ['webapp/WEB-INF/views/' + config.project + '/*.ftl'];
  //批量增加的前缀(生产环境);
  var prefix = "http://z7.tuanimg.com/j/assets";
  //测试环境;
  var prefix_dev = "http://192.168.90.153/j/assets";
  //批量事先被替换的前缀;
  var prefixToReplace = "http://z7.tuanimg.com/j/assets";
  //装替换关系的数组;
  var arr = [];
  var arrCustom = [];
  //通配符转换成grunt.file可以识别的文件;
  files = grunt.file.expand(files);
  //时间统计;
  require('time-grunt')(grunt);
  // Automatically load required grunt tasks
  require('jit-grunt')(grunt, {
    useminPrepare: 'grunt-usemin',
    replace: 'grunt-text-replace'
  });

  // Define the configuration for all the tasks
  grunt.initConfig({

    // Project settings
    config: config,
    //替换url
    replace: {
      production: {
        overwrite: true,
        src: files,
        // dest: '<%= config.dist %>/build/',
        replacements: arr
      },
      before: {
        overwrite: true,
        src: files,
        // dest: '<%= config.dist %>/build/',
        replacements: [{
          from: prefixToReplace,
          to: function(mat) {
            grunt.log.ok("事先替换的字符:" + mat)
            return '';
          }
        }]
      },
      custom: {
        overwrite: true,
        src: ['webapp/WEB-INF/views/<%=config.project%>/*.ftl'],
        replacements: arrCustom
      }
    },


    // Empties folders to start fresh
    clean: {
      dist: {
        files: [{
          dot: true,
          src: [
            '.tmp',
            '<%= config.dist %>/<%= config.app %>/images/<%= config.project %>/*',
            '<%= config.dist %>/<%= config.app %>/javascripts/activity/<%= config.project %>*.js',
            '<%= config.dist %>/<%= config.app %>/stylesheets/activity/<%= config.project %>*.css',
            '<%= config.dist %>/<%= config.app %>/javascripts/common/*.js',
            '!<%= config.dist %>/.git*'
          ]
        }]
      },
      common_bak: {
        files: [{
          dot: true,
          src: [
            '<%= config.dist %>/<%= config.app %>/javascripts/common_bak'
          ]
        }]
      },

    },
    // Renames files for browser caching purposes
    filerev: {
      options: {
        length: 32,
        process: function(basename, name, ext) {
          return basename + "-" + name + "." + ext;
        }
      },
      dist: {
        src: [
          '<%= config.dist %>/<%= config.app %>/images/<%= config.project %>/*',
          '<%= config.dist %>/<%= config.app %>/javascripts/activity/<%= config.project %>.js',
          '<%= config.dist %>/<%= config.app %>/javascripts/common/*.js',
          '<%= config.dist %>/<%= config.app %>/stylesheets/activity/<%= config.project %>.css',
          '!<%= config.dist %>/<%= config.app %>/javascripts/common_bak/*',
        ]
      }
    },

    // Reads HTML for usemin blocks to enable smart builds that automatically
    // concat, minify and revision files. Creates configurations in memory so
    // additional tasks can operate on them
    useminPrepare: {
      options: {
        dest: '<%= config.dist %>/assets',
        //block 搜索的目录;
        root: [
          '<%= config.dist %>/assets'
        ]
      },
      //被搜索的文件;
      html: 'webapp/WEB-INF/views/<%= config.project %>/*.ftl'
    },

    // Performs rewrites based on rev and the useminPrepare configuration
    usemin: {
      options: {
        //引用文件路径在assetsDirs中寻找;
        assetsDirs: [
          '<%= config.dist %>/assets',
        ]
      },
      //被替换的html文件；
      html: files,
      css: ['<%= config.dist %>/stylesheets/activity/.<%= config.project %>.css']
    },

    //压缩html,一般没用；
    htmlmin: {
      dist: {
        options: {
          collapseBooleanAttributes: true,
          collapseWhitespace: true,
          conservativeCollapse: true,
          removeAttributeQuotes: true,
          removeCommentsFromCDATA: true,
          removeEmptyAttributes: true,
          removeOptionalTags: true,
          // true would impact styles with attribute selectors
          removeRedundantAttributes: false,
          useShortDoctype: true
        },
        files: [{
          expand: true,
          cwd: 'webapp/WEB-INF/views/<%= config.project %>',
          src: '{,*/}*.ftl',
          dest: 'webapp/WEB-INF/views/<%= config.project %>'
        }]
      }
    },

    // By default, your `index.html`'s <!-- Usemin block --> will take care
    // of minification. These next options are pre-configured if you do not
    // wish to use the Usemin blocks.
    // cssmin: {
    //   options: {
    //     sourceMap: true
    //   },
    // },
    // uglify: {
    //   options: {
    //     sourceMap: true
    //   }
    // },
    concat: {
      options: {
        // sourceMap: true,
        separator: ';'
      }
    },

    // Copies remaining files to places other tasks can use
    copy: {
      dist: {
        files: [{
          expand: true,
          dot: true,
          cwd: '<%= config.app %>',
          dest: '<%= config.dist %>/<%= config.app %>',
          src: [
            '**/*.*'
          ]
        }]
      },
      //处理后端资源文件，单纯复制；
      dist2: {
        files: [{
          expand: true,
          dot: true,
          cwd: 'admin_assets',
          dest: '<%= config.dist %>/admin_assets',
          src: [
            '**/*.*'
          ]
        }]
      },
      dist_common_bak: {
        files: [{
          expand: true,
          dot: true,
          cwd: '<%= config.dist %>/<%= config.app %>/javascripts/common',
          dest: '<%= config.dist %>/<%= config.app %>/javascripts/common_bak',
          src: [
            '*.js'
          ]
        }]
      },
      dist_common_unbak: {
        files: [{
          expand: true,
          dot: true,
          cwd: '<%= config.dist %>/<%= config.app %>/javascripts/common_bak',
          dest: '<%= config.dist %>/<%= config.app %>/javascripts/common',
          src: [
            '*.js'
          ]
        }]
      },
      bak: {
        files: [{
          expand: true,
          dot: true,
          cwd: 'webapp/WEB-INF/views/<%= config.project %>',
          dest: 'webapp/WEB-INF/views/<%= config.project %>_bak',
          src: [
            '**/*.*'
          ]
        }]
      },
      unbak: {
        files: [{
          expand: true,
          dot: true,
          cwd: 'webapp/WEB-INF/views/<%= config.project %>_bak',
          dest: 'webapp/WEB-INF/views/<%= config.project %>',
          src: [
            '**/*.*'
          ]
        }]
      }
    },
  });

  grunt.registerTask('repl', 'replace  files', function(target, arg1, arg2) {
    console.log(arguments);
    if (target === 'dev') {
      prefix = prefix_dev;
    } else if (target === 'custom') {
      if (!arg1 || !arg2) {
        grunt.fail.warn("参数不能为空");
        return;
      };
      var objCustom = {};
      objCustom.from = arg1;
      objCustom.to = function(mat) {
        console.log(mat);
        return arg2;
      }
      arrCustom.push(objCustom);
      grunt.task.run([
        'replace:custom'
      ]);
      return;
    } else if (target !== 'all') {
      grunt.fail.warn("只能为dev,custom,all...");
      return;
    }

    function detectToRepl(ele, attr) {
      for (var i = 0; i < $(ele).length; i++) {
        var element = $(ele)[i];
        if (!$(element).attr(attr)) {
          continue;
        };
        var href = $(element).attr(attr);
        var obj = {};
        obj.from = href.addCouple();
        obj.to = function(match) {
          match = match.removeCouple().trim();
          if (match.startWith('http') || match.startWith('#')) {
            return match.addCouple();
          }
          if (match.startWith('/')) {
            return (prefix + match).addCouple();
          }
          return (prefix + '/' + match).addCouple();
        };
        var obj2 = {};
        obj2.from = href.addCouple("\'");
        obj2.to = function(match) {
          match = match.removeCouple("\'").trim();
          if (match.startWith('http') || match.startWith('#')) {
            return match.addCouple("\'");
          }
          if (match.startWith('/')) {
            return (prefix + match).addCouple("\'");
          }
          return (prefix + '/' + match).addCouple("\'");
        };
        arr.push(obj);
        arr.push(obj2);
      };

    }
    var cheerio = require('cheerio');
    for (var i = 0; i < files.length; i++) {
      var content = grunt.file.read(files[i]);
      var $ = cheerio.load(content);
      //css
      detectToRepl('link', 'href');
      //js
      detectToRepl('script', 'src');
      //img
      detectToRepl('img', 'src');
      //a
      // detectToRepl('a','href');
      grunt.task.run([
        'replace:production'
      ]);
    };
    // grunt.log.ok(count + "处引用已经被替换!");
  });

  grunt.registerTask('default', "执行任务", function(target) {
    grunt.task.run([
      'copy:dist_common_bak',
      'clean:dist',
      'copy:dist',
      'copy:bak',
      'replace:before',
      'useminPrepare',
      'concat',
      'cssmin',
      'uglify',
      'filerev',
      'usemin',
      'repl:all',
      'copy:dist',
      'copy:dist2',
      // 'copy:dist_common_unbak',
      // 'clean:common_bak'
      // 'htmlmin'
    ]);
  });
};

String.prototype.startWith = function(str) {
  return this.substring(0, str.length) === str;
};
String.prototype.trim = function() {
  return this.replace(/(^\s*)|(\s*$)/g, "");
};
String.prototype.addCouple = function(str) {
  if (undefined === str) {
    str = '\"';
  }
  return str + this + str;
};
String.prototype.removeCouple = function(str) {
  if (undefined === str) {
    str = '\"';
  }
  if (this.substr(0, str.length) === str && this.substr(-1, str.length) === str) {
    return this.substr(str.length, this.length - str.length - 1);
  }
};