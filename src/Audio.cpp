#include "Audio.h"
#include <cstdlib>
#include <stdio.h>
#include <math.h>
#include "portaudio.h"

#define PA_SAMPLE_TYPE      paFloat32
#define FRAMES_PER_BUFFER   (64)

namespace AudioInfo
{
  PaStreamParameters inputParameters, outputParameters;
  PaStream *stream;
  PaError err;
  int gNumNoInputs = 0;
  /* This routine will be called by the PortAudio engine when audio is needed.
  ** It may be called at interrupt level on some machines so don't do anything
  ** that could mess up the system like calling malloc() or free().
  */
  bool record = false;
  int sample_rate;
};

using namespace AudioInfo;

static void AudioErrorCallback()
{
  Pa_Terminate();
  fprintf( stderr, "An error occurred while using the portaudio stream\n" );
  fprintf( stderr, "Error number: %d\n", err );
  fprintf( stderr, "Error message: %s\n", Pa_GetErrorText( err ) );
  exit(0);
}

Audio::Audio()
{
  err = Pa_Initialize();
  if( err != paNoError ) AudioErrorCallback();

  inputParameters.device = Pa_GetDefaultInputDevice(); /* default input device */
  if (inputParameters.device == paNoDevice) {
      fprintf(stderr,"Error: No default input device.\n");
      AudioErrorCallback();
  }
  inputParameters.channelCount = 1;       /* mono input */
  inputParameters.sampleFormat = PA_SAMPLE_TYPE;
  inputParameters.suggestedLatency = Pa_GetDeviceInfo( inputParameters.device )->defaultLowInputLatency;
  inputParameters.hostApiSpecificStreamInfo = NULL;

  outputParameters.device = Pa_GetDefaultOutputDevice(); /* default output device */
  if (outputParameters.device == paNoDevice) {
      fprintf(stderr,"Error: No default output device.\n");
      AudioErrorCallback();
  }
  outputParameters.channelCount = 2;      /* stereo output */
  outputParameters.sampleFormat = PA_SAMPLE_TYPE;
  outputParameters.suggestedLatency = Pa_GetDeviceInfo( outputParameters.device )->defaultLowOutputLatency;
  outputParameters.hostApiSpecificStreamInfo = NULL;

  int numDevices = Pa_GetDeviceCount();
  if (numDevices < 0) {
      fprintf(stderr, "ERROR: Pa_CountDevices returned 0x%x\n", numDevices);
  } else {
      printf("Available audio devices:\n");
      for (int i = 0; i < numDevices; i++) {
          const PaDeviceInfo* deviceInfo = Pa_GetDeviceInfo(i);
          if (!deviceInfo) continue;

          printf("Device %d: %s\n", i, deviceInfo->name);
          printf("  Max input channels: %d\n", deviceInfo->maxInputChannels);
          printf("  Max output channels: %d\n", deviceInfo->maxOutputChannels);
          printf("  Default sample rate: %.2f\n", deviceInfo->defaultSampleRate);
          printf("  Default low input latency: %.4f s\n", deviceInfo->defaultLowInputLatency);
          printf("  Default low output latency: %.4f s\n", deviceInfo->defaultLowOutputLatency);
          printf("  Default high input latency: %.4f s\n", deviceInfo->defaultHighInputLatency);
          printf("  Default high output latency: %.4f s\n", deviceInfo->defaultHighOutputLatency);
          printf("\n");
      }
  }

  PaDeviceIndex defaultInput = Pa_GetDefaultInputDevice();
  PaDeviceIndex defaultOutput = Pa_GetDefaultOutputDevice();
  printf("Default input device index: %d\n", defaultInput);
  printf("Default output device index: %d\n", defaultOutput);
}

Audio::~Audio()
{
  printf("Finished. gNumNoInputs = %d\n", gNumNoInputs );
  Pa_Terminate();
}

typedef float SAMPLE;

static int fuzzCallback( const void *inputBuffer, void *outputBuffer,
                         unsigned long framesPerBuffer,
                         const PaStreamCallbackTimeInfo* timeInfo,
                         PaStreamCallbackFlags statusFlags,
                         void *userData );

/* Non-linear amplifier with soft distortion curve. */
inline float CubicAmplifier( float input )
{
    float output, temp;
    if( input < 0.0 )
    {
        temp = input + 1.0f;
        output = (temp * temp * temp) - 1.0f;
    }
    else
    {
        temp = input - 1.0f;
        output = (temp * temp * temp) + 1.0f;
    }

    return output;
}
#define FUZZ(x) CubicAmplifier(CubicAmplifier(CubicAmplifier(CubicAmplifier(x))))

static int fuzzCallback( const void *inputBuffer, void *outputBuffer,
                         unsigned long framesPerBuffer,
                         const PaStreamCallbackTimeInfo* timeInfo,
                         PaStreamCallbackFlags statusFlags,
                         void *userData )
{
    SAMPLE *out = (SAMPLE*)outputBuffer;
    const SAMPLE *in = (const SAMPLE*)inputBuffer;
    unsigned int i;
    (void) timeInfo; /* Prevent unused variable warnings. */
    (void) statusFlags;
    (void) userData;

    if( inputBuffer == NULL )
    {
        for( i=0; i<framesPerBuffer; i++ )
        {
            *out++ = 0;  /* left - silent */
            *out++ = 0;  /* right - silent */
        }
        gNumNoInputs += 1;
    }
    else
    {
        for( i=0; i<framesPerBuffer; i++ )
        {
            SAMPLE sample = *in++; /* MONO input */
            *out++ = FUZZ(sample); /* left - distorted */
            *out++ = sample;       /* right - clean */
        }
    }

    return paContinue;
}

void Audio::Toggle()
{
  record = !record;

  if(record)
  {
    err = Pa_OpenStream(
              &stream,
              &inputParameters,
              &outputParameters,
              sample_rate,
              FRAMES_PER_BUFFER,
              0, /* paClipOff, */  /* we won't output out of range samples so don't bother clipping them */
              fuzzCallback,
              NULL );
    if( err != paNoError ) AudioErrorCallback();

    err = Pa_StartStream( stream );
    if( err != paNoError ) AudioErrorCallback();
  }

  if(!record)
  {
    err = Pa_CloseStream( stream );
    if( err != paNoError ) AudioErrorCallback();
  }
}

void Audio::SetSR(Sample_Rate sr)
{
  sample_rate = static_cast<int>(sr);
}


