const gulp = require('gulp');
const sass = require('gulp-sass');
sass.compiler = require('node-sass');
const sourcemaps = require('gulp-sourcemaps');
const rename = require('gulp-rename');
const header = require('gulp-header');
const replace = require('gulp-string-replace');
const concatCss = require('gulp-concat-css');
const del = require('del');
path = require('path');

const basepath = './src/assets/styles/';
const fontspath = basepath + 'fonts/';
const infontspath = basepath + 'inlinefonts/';
const temppath = './src/assets/temp/';
const repopath = './src/assets/sb-repo/';
replaceBeforeRegex = /^(.*)\/\*\!Delete\ before\ this\*\//mi;

/*========================
  Fonts CSS generate
==========================*/
/* Generate Fonts CSS files */
function fontsCss() {
    return (
        gulp.src([infontspath + '**/*.scss', '!' + infontspath + 'fonts.scss'])
            .pipe(sass({ outputStyle: 'compressed' })).on("error", sass.logError)
            .pipe(gulp.dest(repopath + 'fonts'))
    );
}
exports.fontsCss = fontsCss;

/* Generate Concated Fonts CSS files */
function fontsConcated() {
    return (
        gulp.src([infontspath + 'fonts.scss'])
            .pipe(sass({ outputStyle: 'compressed' })).on("error", sass.logError)
            .pipe(gulp.dest(repopath))
    );
}
exports.fontsConcated = fontsConcated;

/*========================
  Main CSS file generation
==========================*/
/* Generate Main CSS files from styles folder */
function scssMain() {
    return (
        gulp.src(basepath + 'styles.scss')
            .pipe(sourcemaps.init())
            .pipe(sass({ outputStyle: 'compressed' })).on("error", sass.logError)
            .pipe(gulp.dest(repopath))
    );
}
exports.scssMain = scssMain;

/*========================
  Generate Variables file
==========================*/
function scssVariables() {
    return (
        gulp.src([basepath + '_variables.scss'])
            .pipe(rename(function(path) { //since sass() wont compile _partials files we are removing it here
                path.basename = path.basename[0] == '_' ? path.basename.substr(1) : path.basename;
            }))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(rename(function(file) {
                const parentFolder = path.dirname(file.dirname);
                file.dirname = path.join(parentFolder, ''); //generated file extension
            }))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssVariables = scssVariables;

/*========================
  Generate Base file
==========================*/
function scssBase() {
    return (
        gulp.src([basepath + 'base/base.scss'])
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssBase = scssBase;

/*========================
  Generate Layout file
==========================*/
function scssLayout() {
    return (
        gulp.src([basepath + 'layout/layout.scss'])
            .pipe(header('@import \'../global\';\n'))
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssLayout = scssLayout;

/*========================
  Generate Pages file
==========================*/
// Individual
function scssPages() {
    return (
        gulp.src([basepath + 'pages/*.scss','!' + basepath + 'pages/pages.scss'])
            .pipe(rename(function(path) { //since sass() wont compile _partials files we are removing it here
                path.basename = path.basename[0] == '_' ? path.basename.substr(1) : path.basename;
            }))
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(rename(function(file) {
                const parentFolder = path.dirname(file.dirname);
                file.dirname = path.join(parentFolder, 'pages'); //generated file extension
            }))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssPages = scssPages;

// Concated
function scssPagesConcated() {
    return (
        gulp.src([basepath + 'pages/pages.scss'])
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssPagesConcated = scssPagesConcated;

/*========================
  Generate vendors file
==========================*/
// Individual
function scssVendors() {
    return (
        gulp.src([basepath + 'vendors/*.scss','!' + basepath + 'vendors/vendors.scss'])
            .pipe(rename(function(path) { //since sass() wont compile _partials files we are removing it here
                path.basename = path.basename[0] == '_' ? path.basename.substr(1) : path.basename;
            }))
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(rename(function(file) {
                const parentFolder = path.dirname(file.dirname);
                file.dirname = path.join(parentFolder, 'vendors'); //generated file extension
            }))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssVendors = scssVendors;

// Concated
function scssVendorsConcated() {
    return (
        gulp.src([basepath + 'vendors/vendors.scss'])
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssVendorsConcated = scssVendorsConcated;

/*========================
  Generate Common file
==========================*/
function scssCommon() {
    return (
        gulp.src([repopath + 'base.css', repopath + 'layout.css'])
            .pipe(concatCss("common.css"))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssCommon = scssCommon;

/*========================
  Component CSS Generation
==========================*/
/* Temp Folder - Copy all other files with imports appended at head*/
function appendImport() {
    return (
        gulp.src([basepath + '**/*.scss', '!' + basepath + '*.scss', '!' + basepath + 'mixins/**/*.scss','!' + basepath + 'fonts/**/*.scss', '!' + basepath + 'inlinefonts/**/*.scss'])
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(gulp.dest(temppath))
    );
}
exports.appendImport = appendImport;

/* Temp Folder - Copy files of level 1*/
function copyStyle() {
    return (
        gulp.src([basepath + '_variables.scss'])
            .pipe(gulp.dest(temppath))
    );
}
exports.copyStyle = copyStyle;

/* Temp Folder - Copy mixins folder*/
function copyMixins() {
    return (
        gulp.src([basepath + 'mixins/**/*'])
            .pipe(gulp.dest(temppath + 'mixins'))
    );
}
exports.copyMixins = copyMixins;

/* Temp Folder - Copy mixins folder*/
function copyFonts() {
    return (
        gulp.src([basepath + 'inlinefonts/**/*'])
            .pipe(gulp.dest(temppath + 'inlinefonts'))
    );
}
exports.copyFonts = copyFonts;

/* Generate Components CSS files from temp folder */
function scssComponents() {
    return (
        gulp.src([temppath + 'components/*.scss', '!' + temppath + 'components/components.scss'])
            .pipe(rename(function(path) { //since sass() wont compile _partials files we are removing it here
                path.basename = path.basename[0] == '_' ? path.basename.substr(1) : path.basename;
            }))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(sourcemaps.write('./'))
            .pipe(rename(function(file) {
                const parentFolder = path.dirname(file.dirname);
                file.dirname = path.join(parentFolder, 'components'); //generated file extension
            }))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssComponents = scssComponents;

/* Generate Concated Components CSS files from temp folder */
function scssComponentsConcated() {
    return (
        gulp.src([basepath + 'components/components.scss'])
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssComponentsConcated = scssComponentsConcated;

/*========================
  Generate Semantic file
==========================*/
function scssSemantic() {
    return (
        gulp.src([basepath + 'semantic/semantic-merged.scss'])
            .pipe(header('/*!Delete before this*/'))
            .pipe(header('@import \'../mixins/mixins\';\n'))
            .pipe(header('@import \'../variables\';\n'))
            .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
            .pipe(replace(replaceBeforeRegex, ''))
            .pipe(gulp.dest(repopath))
    );
}
exports.scssSemantic = scssSemantic;

/*========================
    Cleanup
==========================*/
/* Delete temp and dist folder before regenerate */
const clear = del.bind(null, [repopath,temppath]);

exports.clear = clear;

/*========================
    Watcher
==========================*/
/* Sequence of tasks*/
const sequence = gulp.series( clear, appendImport, fontsCss, fontsConcated, copyStyle, scssVariables, copyMixins, scssComponents, scssComponentsConcated, scssSemantic, scssBase, scssLayout, scssCommon, scssPages, scssPagesConcated, scssVendors, scssVendorsConcated, scssMain);

exports.sequence = sequence;

/* Watch file changes */
function watch(){
    gulp.watch(basepath + '**/*.scss', sequence);
}
exports.watch = watch;

/* Default task */

//gulp.task('default', ['sequence','watch']);
const defaultTask = gulp.series( sequence, watch);

exports.default = defaultTask;