
#ifdef __CUDA__
#define DEVICE_RUNNABLE __host__ __device__
#else
#define DEVICE_RUNNABLE
#endif