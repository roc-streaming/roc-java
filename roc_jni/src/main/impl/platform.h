#pragma once

#ifdef __GNUC__
#define ATTR_PRINTF(n, m) __attribute__((format(printf, n, m)))
#else
#define ATTR_PRINTF(n, m)
#endif

#ifdef __GNUC__
#define ATTR_NODISCARD __attribute__((__warn_unused_result__))
#else
#define ATTR_NODISCARD
#endif
