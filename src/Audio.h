#pragma once

enum class Sample_Rate
{
  SR_22050 = 22050,
  SR_32000 = 32000,
  SR_44100 = 44100,
  SR_48000 = 48000,
  SR_88200 = 88200,
  SR_96000 = 96000
};

struct Audio
{
  Audio();
  ~Audio();
  void Toggle();
  void Volume();
  void SetSR(Sample_Rate sr);
};



