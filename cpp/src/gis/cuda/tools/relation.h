// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

#pragma once
#include <thrust/pair.h>

#include "gis/cuda/common/gis_definitions.h"
#include "gis/cuda/container/kernel_vector.h"
#include "gis/cuda/tools/de9im_matrix.h"
#include "gis/cuda/tools/relation.h"

namespace arctern {
namespace gis {
namespace cuda {
struct LineRelationResult {
  // C(losure) is union(I, B), or complement of E
  int CC;             // dimension of RR
  bool is_coveredby;  // is line coveredby LineString
  int cross_count;
};

struct PointInPolygonResult {
  bool is_in;
  bool is_at_edge;
};

struct KernelBuffer {
  KernelVector<thrust::pair<double, double>> ranges;
};

}  // namespace cuda
}  // namespace gis
}  // namespace arctern
#include "gis/cuda/tools/relation.impl.h"
