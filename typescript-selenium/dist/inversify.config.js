"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var types_1 = require("./types");
var ChromeDriver_1 = require("./driver/ChromeDriver");
var DriverHelper_1 = require("./utils/DriverHelper");
var e2eContainer = new inversify_1.Container();
exports.e2eContainer = e2eContainer;
e2eContainer.bind(types_1.TYPES.Driver).to(ChromeDriver_1.ChromeDriver).inSingletonScope();
e2eContainer.bind('DriverHelper').to(DriverHelper_1.DriverHelper);