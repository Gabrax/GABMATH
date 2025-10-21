cmake_minimum_required(VERSION 3.0)
project(${EXE_NAME})
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# include(FetchContent)
# set(FETCHCONTENT_QUIET FALSE)
#
#
# FetchContent_MakeAvailable()

file(GLOB_RECURSE PROJECT_SOURCES CONFIGURE_DEPENDS "${CMAKE_CURRENT_LIST_DIR}/src/*.c") # Define PROJECT_SOURCES as a list of all source files
set(PROJECT_INCLUDE "${CMAKE_CURRENT_LIST_DIR}/src/") # Define PROJECT_INCLUDE to be the path to the include directory of the project

add_executable("${CMAKE_PROJECT_NAME}")
target_sources(${PROJECT_NAME} PRIVATE ${PROJECT_SOURCES})
target_include_directories(${PROJECT_NAME} PRIVATE ${PROJECT_INCLUDE})
# target_link_libraries(${PROJECT_NAME} PRIVATE )
